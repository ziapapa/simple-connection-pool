package connectionpool;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolException extends SQLException {
    private static final long serialVersionUID = -5581093699395082874L;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolException.class);

    ConnectionPoolException() {
    }

    ConnectionPoolException(String messageId) {
        super(messageId);
        logger.error("Exception Created: " + messageId);
    }

    ConnectionPoolException(String messageId, Throwable exception) {
        super(messageId, exception);
        logger.error("Exception Created: " + messageId, exception);
    }
}
