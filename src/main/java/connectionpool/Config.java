package connectionpool;

public interface Config {
    public String getPoolName();

    public int getMaxConnections();

    public int getMinConnections();

    public int getIncrement();

    public String getUserName();

    public String getPassword();

    public String getUrl();

    public String getDriverClassName();

    public boolean isDefaultPool();

    public long getConnectionWaitTimeOut();

    public String getValidationQuery();
}
