package connectionpool;

public interface PoolMonitor {
    public int getCurrentPoolSize();

    public int getFreeConnections();

    public int getUseConnections();

    public Config getConfigMonitor();
    
    public void stopPooling();
    
    public void startPooling();
}
