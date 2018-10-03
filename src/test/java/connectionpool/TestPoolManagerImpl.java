package connectionpool;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestPoolManagerImpl {
    
    String jsonFile = "C:\\\\eclipse-workspace\\\\connection-pool-test\\\\src\\\\main\\\\resources\\\\dbpool.json";

    @Test
    public void testPoolManagerImpl() throws ConnectionPoolException {
        PoolManagerImpl pm = new PoolManagerImpl(jsonFile);
        assertNotNull(pm);
    }

    @Test
    public void testPoolManagerImplGetConnection() throws ConnectionPoolException {
        PoolManagerImpl pm = new PoolManagerImpl(jsonFile);
        assertNotNull(pm.getPool().getConnection());
    }

}
