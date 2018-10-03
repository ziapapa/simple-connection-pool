package connectionpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolImpl implements ConnectionPool, PoolMonitor {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolImpl.class);
    private Map<Connection, Boolean> connectionMap = new ConcurrentHashMap<Connection, Boolean>();
    private PoolConfigInfo config;
    private final String name;
    private volatile int currentPoolSize = 0;
    private volatile int usedConnections = 0;
    private String validationQuery = null;
    private volatile boolean initBool = false;
    private volatile boolean isPooling = true;

    ConnectionPoolImpl(PoolConfigInfo config) throws ConnectionPoolException {
        this.config = config;
        if (!initBool) {
            initialiseConnections();
        }
        name = "ConnectionPool-" + config.getPoolName();
        validationQuery = config.getValidationQuery();
    }

    private synchronized void initialiseConnections() throws ConnectionPoolException {
        try {
            int minConnections = config.getMinConnections();
            for (int i = 0; i < minConnections; i++) {
                connectionMap.put(loadConnection(), true);
            }
            initBool = true;
            logger.info("init connection [" + config.getPoolName() + "]");
        } catch (Exception e) {
            throw new ConnectionPoolException("Could not load initial connection", e);
        }
    }

    private Connection loadConnection() throws ConnectionPoolException {
        try {
            Class.forName(config.getDriverClassName());
        } catch (ClassNotFoundException classNotFound) {
            throw new ConnectionPoolException("Could not load Driver", classNotFound);
        }
        Connection con = null;
        try {
            con = DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassword());
        } catch (Exception e) {
            throw new ConnectionPoolException("Could not obtain Connection", e);
        }
        currentPoolSize++;

        return con;
    }

    public int getUseConnections() {
        return usedConnections;
    }

    public int getFreeConnections() {
        return currentPoolSize - usedConnections;
    }

    public Config getConfigMonitor() {
        return config;
    }

    public int getCurrentPoolSize() {
        return currentPoolSize;
    }

    public Connection getConnection() throws ConnectionPoolException {

        synchronized (connectionMap) {
            if (!isPooling) throw new StopPoolingException("Stop pooling");
            
            if (config.getMaxConnections() <= getUseConnections()) {
                try {

                    logger.info("Waiting connection : " + config.getConnectionWaitTimeOut() + "ms");
                    connectionMap.wait(config.getConnectionWaitTimeOut());

                    if (config.getMaxConnections() <= getUseConnections()) {
                        throw new TimeOutException("Timed-out while waiting for free connection");
                    }
                    logger.info("Free connection can be obtained");
                } catch (InterruptedException e) {
                    throw new ConnectionPoolException("wait", e);
                }
            }

            Enumeration<Connection> cons = ((ConcurrentHashMap<Connection, Boolean>) connectionMap).keys();

            while (cons.hasMoreElements()) {
                Connection con = cons.nextElement();
                Boolean useBool = connectionMap.get(con);
                if (useBool) {
                    connectionMap.put(con, false);
                    usedConnections++;
                    logger.debug("usedConnections : " + getUseConnections());
                    if (checkIfValid(con)) {
                        return con;
                    }
                    boolean valid = false;
                    int failCounter = 1;
                    while (!valid) {
                        connectionMap.remove(con);
                        currentPoolSize--;
                        con = loadConnection();
                        connectionMap.put(con, false);
                        failCounter++;
                        valid = checkIfValid(con);
                        if ((failCounter == 3) && (!valid)) {
                            throw new ConnectionPoolException(
                                    "Three consecutive connections failed the Validator Query test");
                        }
                    }
                    logger.debug("failCounter : " + failCounter);
                    return con;
                }
            }
            int increment = config.getIncrement();
            Connection con = null;
            for (int i = 0; i < increment && i + currentPoolSize <= config.getMaxConnections(); i++) {
                con = loadConnection();
                boolean valid = checkIfValid(con);
                int failCounter = 1;
                while (!valid) {
                    con = loadConnection();
                    failCounter++;
                    valid = checkIfValid(con);
                    if ((failCounter == 3) && (!valid)) {
                        throw new ConnectionPoolException(
                                "Three consecutive connections failed the Validator Query test");
                    }
                }
                if (i == 0) {
                    connectionMap.put(con, false);

                } else {
                    connectionMap.put(con, true);
                }
            }
            usedConnections++;
            return con;
        }
    }

    public void returnConnection(Connection retcon) {
        synchronized (connectionMap) {
            Enumeration<Connection> cons = ((ConcurrentHashMap<Connection, Boolean>) connectionMap).keys();

            while (cons.hasMoreElements()) {
                Connection con = cons.nextElement();
                if (con == retcon) {
                    connectionMap.put(con, true);
                    usedConnections--;
                    logger.debug("Connection Released Free : " + getFreeConnections());
                    connectionMap.notifyAll();
                    break;
                }
            }
        }
    }

    private boolean checkIfValid(Connection conn) {
        boolean bool;
        try {
            Statement stmt;
            if (validationQuery != null && !validationQuery.trim().equals("")) {
                stmt = conn.createStatement();
                bool = stmt.execute(validationQuery);
                stmt.close();
                return bool;
            }
            return true;
        } catch (SQLException e) {
            logger.error("Exception occuured in Validation returning false", e);
            bool = false;
        }
        return bool;
    }

    public void stopPooling() {
        isPooling = false;
        logger.info("stop pooling requset");
    }
    
    public void startPooling() {
        isPooling = true;
        logger.info("start pooling requset");
    }
}
