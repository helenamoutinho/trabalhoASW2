package rsa;

public class RideSharingAppException extends Exception {

    public RideSharingAppException() {}
    public RideSharingAppException(String message) { super(message); }
    public RideSharingAppException(Throwable cause) { super(cause); }
    public RideSharingAppException(String message, Throwable cause) { super(message, cause); }
    public RideSharingAppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
