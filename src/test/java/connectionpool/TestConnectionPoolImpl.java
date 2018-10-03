package connectionpool;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestConnectionPoolImpl {

    PoolConfigInfo pci = new PoolConfigInfo();
    int maxConnections = 10;
    int minConnections = 5;

    @Before
    public void setUp() throws Exception {
        pci.setPoolName("db1");
        pci.setMaxConnections(maxConnections);
        pci.setMinConnections(minConnections);
        pci.setConnectionWaitTimeOut(1000L);
        pci.setIncrement(1);
        pci.setDriverClassName("com.mysql.cj.jdbc.Driver");
        pci.setUserName("testA");
        pci.setPassword("test1234");
        pci.setUrl("jdbc:mysql://localhost:3306/world?serverTimezone=UTC&useSSL=false&autoReconnect=true&validationQuery=select 1");
        pci.setValidationQuery("select 1");
        pci.setDefaultPool("true");
    }

    @Test
    public void testConnectionPoolImpl() throws ConnectionPoolException {
        ConnectionPoolImpl cp = new ConnectionPoolImpl(pci);
        assertNotNull(cp.getConnection());
    }

    @Test
    public void testGetCurrentPoolSize() throws ConnectionPoolException {
        ConnectionPoolImpl cp = new ConnectionPoolImpl(pci);
        assertEquals(minConnections, cp.getCurrentPoolSize());
    }
}
