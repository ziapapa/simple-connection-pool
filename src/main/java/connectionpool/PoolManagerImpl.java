package connectionpool;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoolManagerImpl implements PoolManager {

    private static final Logger logger = LoggerFactory.getLogger(PoolManagerImpl.class);

    private Map<String, ConnectionPoolImpl> poolMap = new ConcurrentHashMap<String, ConnectionPoolImpl>();
    private String defaultPool;

    public PoolManagerImpl(String fileName) throws ConnectionPoolException {
        if ((fileName == null) || (fileName.trim().equals(""))) {
            throw new IllegalArgumentException("File Name cannot be null/empty");
        }
        File f1 = new File(fileName);
        loadConfig(f1);
    }

    public PoolManagerImpl(File file) throws ConnectionPoolException {
        loadConfig(file);
    }

    private void loadConfig(File file) throws ConnectionPoolException {
        List<Config> configList = new ConfigFileParser().getPoolConfig(file);
        Iterator<Config> it = configList.iterator();
        while (it.hasNext()) {
            PoolConfigInfo pc = (PoolConfigInfo) it.next();
            String poolName = pc.getPoolName();
            logger.debug(pc + "");
            if (pc.isDefaultPool()) {
                if (defaultPool != null) {
                    throw new ConnectionPoolException(
                            "More than one Connection Pools cannot have default set to 'true'");
                }
                defaultPool = poolName;
            }
            poolMap.put(poolName, new ConnectionPoolImpl(pc));
        }
    }

    public ConnectionPool getPool() throws ConnectionPoolException {
        if (defaultPool == null) {
            throw new ConnectionPoolException("No default pool specified");
        }
        return getPool(defaultPool);
    }

    public ConnectionPool getPool(String poolName) throws ConnectionPoolException {
        ConnectionPool connectionPool = poolMap.get(poolName);
        if (connectionPool == null) {
            throw new ConnectionPoolException("No such pool:" + poolName);
        }
        return connectionPool;
    }

    public PoolMonitor getPoolMonitor() throws ConnectionPoolException {
        if (defaultPool == null) {
            throw new ConnectionPoolException("No default pool specified");
        }
        return getPoolMonitor(defaultPool);
    }

    public PoolMonitor getPoolMonitor(String poolName) throws ConnectionPoolException {
        PoolMonitor poolMonitor = poolMap.get(poolName);
        if (poolMonitor == null) {
            throw new ConnectionPoolException("No such pool:" + poolName);
        }
        return poolMonitor;
    }

    public static void main(String[] args) throws Exception {
        String jsonFile = "C:\\\\eclipse-workspace\\\\connection-pool-test\\\\src\\\\main\\\\resources\\\\dbpool.json";
        int k = 0;
        Thread[] arr = new Thread[40];
        try {
            PoolManagerImpl pm = new PoolManagerImpl(jsonFile);
            //System.exit(0);
            for (int i = 0; i < 20; i++) {
                Thread r = new Thread(new ThreadRunner(pm, "db1", i * (int)(Math.random() * 100) + 1));

                arr[k] = r;
                k++;
                r.start();
                r = new Thread(new ThreadRunner(pm, "db2", i * (int)(Math.random() * 100) + 1));

                r.start();
                arr[k] = r;
                k++;
            }
        } catch (Exception e) {
            logger.error("main", e);
        }
        for (int z = 0; z < k; z++) {
            System.out.println("waiting for thread " + z);
            arr[z].join();
        }
        System.out.println("Child threads Finished -->Exiting");
    }

    public static class ThreadRunner implements Runnable {
        PoolManager p1;
        String poolName;
        int sleepTime;

        public ThreadRunner(PoolManager p1, String poolName, int sleepTime) {
            this.p1 = p1;
            this.poolName = poolName;
            this.sleepTime = sleepTime;
        }

        public void run() {
            ConnectionPool cp = null;
            Connection conn = null;
            try {
                for (int i = 0; i <= 10; i++) {
                    System.out.println("Thread " + poolName + " running - "+ i );
                    System.out.println("poolsize:" + p1.getPoolMonitor(poolName).getCurrentPoolSize() + "/free:" + p1.getPoolMonitor(poolName).getFreeConnections() + "/Use:" + p1.getPoolMonitor(poolName).getUseConnections());
                    cp = p1.getPool(poolName);
                    conn = cp.getConnection();

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cp.returnConnection(conn);
                    System.out.println("poolsize:" + p1.getPoolMonitor(poolName).getCurrentPoolSize() + "/free:" + p1.getPoolMonitor(poolName).getFreeConnections() + "/Use:" + p1.getPoolMonitor(poolName).getUseConnections());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
