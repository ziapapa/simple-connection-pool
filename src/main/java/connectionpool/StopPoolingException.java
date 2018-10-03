package connectionpool;

public class StopPoolingException extends ConnectionPoolException {
    public StopPoolingException() {
    }

    public StopPoolingException(String msg) {
        super(msg);
    }
}
