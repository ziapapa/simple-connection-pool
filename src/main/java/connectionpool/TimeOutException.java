package connectionpool;

public class TimeOutException extends ConnectionPoolException {
    public TimeOutException() {
    }

    public TimeOutException(String msg) {
        super(msg);
    }
}
