package ru.kamuzta.rollfactorymgr.exception;

public class UserFriendlyException extends RuntimeException {
    public UserFriendlyException() {
        super();
    }

    public UserFriendlyException(String message) {
        super(message);
    }

    public UserFriendlyException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public UserFriendlyException(String message, Throwable cause) {
        super(message, cause);
    }
}
