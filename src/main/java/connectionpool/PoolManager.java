package connectionpool;

public interface PoolManager {
    public ConnectionPool getPool() throws ConnectionPoolException;

    public ConnectionPool getPool(String poolName) throws ConnectionPoolException;

    public PoolMonitor getPoolMonitor() throws ConnectionPoolException;

    public PoolMonitor getPoolMonitor(String poolName) throws ConnectionPoolException;
}
