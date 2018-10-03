package connectionpool;

import java.sql.Connection;
import java.util.Vector;

public interface ConnectionPool {
    public Connection getConnection() throws ConnectionPoolException;

    public void returnConnection(Connection conn);
}
