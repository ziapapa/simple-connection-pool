package connectionpool;

public class PoolConfigInfo implements Config {
    private String poolName;
    private int maxConnections;
    private int minConnections;
    private int increment;
    private String userName;
    private String password;
    private String url;
    private String driverClassName;
    private long connectionWaitTimeOut = 5000L;
    private String validationQuery = null;
    private boolean defaultPool = false;

    public String getPoolName() {
        return this.poolName;
    }

    void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMinConnections() {
        return this.minConnections;
    }

    void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    public int getIncrement() {
        return this.increment;
    }

    void setIncrement(int increment) {
        this.increment = increment;
    }

    public String getUserName() {
        return this.userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return this.url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public boolean isDefaultPool() {
        return this.defaultPool;
    }

    void setDefaultPool(String defaultPool) {
        this.defaultPool = Boolean.parseBoolean(defaultPool);
    }

    public long getConnectionWaitTimeOut() {
        return this.connectionWaitTimeOut;
    }

    void setConnectionWaitTimeOut(long connectionWaitTimeOut) {
        this.connectionWaitTimeOut = connectionWaitTimeOut;
    }

    void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public String getValidationQuery() {
        return this.validationQuery;
    }

    @Override
    public String toString() {
        return "PoolConfigInfo [poolName=" + poolName + ", maxConnections=" + maxConnections + ", minConnections="
                + minConnections + ", increment=" + increment + ", userName=" + userName + ", password=" + password
                + ", url=" + url + ", driverClassName=" + driverClassName + ", connectionWaitTimeOut="
                + connectionWaitTimeOut + ", validationQuery=" + validationQuery + ", defaultPool=" + defaultPool + "]";
    }
}
