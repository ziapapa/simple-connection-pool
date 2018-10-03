package connectionpool;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PoolFactory {
    private static PoolManager pm = null;

    public PoolFactory(File file) throws ConnectionPoolException {
        if (pm != null) {
            throw new ConnectionPoolException(
                    "This Class follows a Singleton Pattern. Instance has already been created and initialized.");
        }
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        startPool(file);
    }

    public PoolFactory(String fileName) throws ConnectionPoolException {
        if (pm != null) {
            throw new ConnectionPoolException("This Class follows a Singleton Pattern. Instance has already been created and initialized.");
        }
        if ((fileName == null) || (fileName.trim().equals(""))) {
            throw new IllegalArgumentException("filename cannot be null/empty");
        }
        File file = new File(fileName);
        startPool(file);
    }

    private void startPool(File file) throws ConnectionPoolException {
        if (pm == null) {
            pm = new PoolManagerImpl(file);
        }
    }

    private static void checkAndThrow() throws ConnectionPoolException {
        if (pm == null) {
            throw new ConnectionPoolException(
                    "PoolManager is not initialised ,Please create an object first with the configuration");
        }
    }

    public static ConnectionPool getPool() throws ConnectionPoolException {
        checkAndThrow();

        return pm.getPool();
    }

    public static ConnectionPool getPool(String poolName) throws ConnectionPoolException {
        checkAndThrow();

        return pm.getPool(poolName);
    }

    public static PoolMonitor getPoolMonitor() throws ConnectionPoolException {
        checkAndThrow();

        return pm.getPoolMonitor();
    }

    public static PoolMonitor getPoolMonitor(String poolName) throws ConnectionPoolException {
        checkAndThrow();

        return pm.getPoolMonitor(poolName);
    }

    public static void main(String[] arg) throws Exception {
        PoolFactory pf = new PoolFactory("C:\\eclipse-workspace\\connection-pool-test\\src\\main\\resources\\dbpool.json");
        
        ConnectionPool cp = PoolFactory.getPool();
        PoolMonitor cm = PoolFactory.getPoolMonitor();
        Connection conn = cp.getConnection();
        
        //monitor after getConnection 
        System.out.println("CurrentPoolSize : " + cm.getCurrentPoolSize());
        System.out.println("FreeConnections : " + cm.getFreeConnections());
        System.out.println("UseConnections : " + cm.getUseConnections());
        
        Statement stmt = conn.createStatement();
        boolean bool = stmt.execute(cm.getConfigMonitor().getValidationQuery());
        System.out.println("execute : " + bool);
        stmt.close();
        cp.returnConnection(conn);
        
        //monitor after returnConnection
        System.out.println("CurrentPoolSize : " + cm.getCurrentPoolSize());
        System.out.println("FreeConnections : " + cm.getFreeConnections());
        System.out.println("UseConnections : " + cm.getUseConnections());
    }
}
