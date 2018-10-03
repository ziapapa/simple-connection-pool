package connectionpool;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestPoolFactory {

    static String jsonFile = "C:\\\\eclipse-workspace\\\\connection-pool-test\\\\src\\\\main\\\\resources\\\\dbpool.json";
    static PoolFactory pf;
    static ConnectionPool cp;
    static PoolMonitor cm;

    @BeforeClass
    public static void setUp() throws Exception {
        pf = new PoolFactory(jsonFile);
        cp = PoolFactory.getPool();
        cm = PoolFactory.getPoolMonitor();
    }

    @Test
    public void testPoolFactoryBasic() throws SQLException {

        Connection conn = cp.getConnection();

        // monitor after getConnection
        System.out.println("CurrentPoolSize : " + cm.getCurrentPoolSize());
        System.out.println("FreeConnections : " + cm.getFreeConnections());
        System.out.println("UseConnections : " + cm.getUseConnections());

        Statement stmt = conn.createStatement();
        boolean bool = stmt.execute(cm.getConfigMonitor().getValidationQuery());
        System.out.println("execute : " + bool);
        stmt.close();
        cp.returnConnection(conn);

        // monitor after returnConnection
        System.out.println("CurrentPoolSize : " + cm.getCurrentPoolSize());
        System.out.println("FreeConnections : " + cm.getFreeConnections());
        System.out.println("UseConnections : " + cm.getUseConnections());
    }

    @Test
    public void testPoolFactoryStopPooling(){
        cm.stopPooling();
        Connection conn = null;
        try {
            cp.getConnection();
        } catch (ConnectionPoolException e) {
            assertEquals(StopPoolingException.class, e.getClass());
        }
        cp.returnConnection(conn);
        cm.startPooling();
        
        System.out.println("CurrentPoolSize : " + cm.getCurrentPoolSize());
        System.out.println("FreeConnections : " + cm.getFreeConnections());
        System.out.println("UseConnections : " + cm.getUseConnections());
    }
    
    @Test
    public void testPoolFactoryThread() throws SQLException, InterruptedException {
        int k = 0;
        Thread[] arr = new Thread[40];
        for (int i = 0; i < 20; i++) {
            Thread r = new Thread(new ThreadRunner(pf, "db1", i * (int) (Math.random() * 100) + 1));

            arr[k] = r;
            k++;
            r.start();
            r = new Thread(new ThreadRunner(pf, "db2", i * (int) (Math.random() * 100) + 1));

            r.start();
            arr[k] = r;
            k++;
        }

        for (int z = 0; z < k; z++) {
            System.out.println("waiting for thread " + z);
            arr[z].join();
        }
        System.out.println("Child threads Finished -->Exiting");
        
        assertEquals(PoolFactory.getPoolMonitor("db1").getConfigMonitor().getMaxConnections(), PoolFactory.getPoolMonitor("db1").getFreeConnections());
        assertEquals(PoolFactory.getPoolMonitor("db2").getConfigMonitor().getMaxConnections(), PoolFactory.getPoolMonitor("db2").getFreeConnections());
    }

    public static class ThreadRunner implements Runnable {
        PoolFactory pf;
        String poolName;
        int sleepTime;

        public ThreadRunner(PoolFactory pf, String poolName, int sleepTime) {
            this.pf = pf;
            this.poolName = poolName;
            this.sleepTime = sleepTime;
        }

        public void run() {
            Connection conn = null;
            try {
                ConnectionPool cp = PoolFactory.getPool(poolName);
                PoolMonitor cm = PoolFactory.getPoolMonitor(poolName);
                
                for (int i = 0; i <= 10; i++) {
                    System.out.println("Thread " + poolName + " running - " + i);
                    /*System.out.println("start poolName:" + poolName
                            + " - poolsize:" + cm.getCurrentPoolSize()
                            + "/free:" + cm.getFreeConnections()
                            + "/Use:"  + cm.getUseConnections());*/
                    conn = cp.getConnection();

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cp.returnConnection(conn);
                    /*System.out.println("end poolName:" + poolName
                            + " - poolsize:" + cm.getCurrentPoolSize()
                            + "/free:" + cm.getFreeConnections()
                            + "/Use:"  + cm.getUseConnections());*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
