package ru.kamuzta.rollfactorymgr.exception;

public class NotRuntimeExceptionWrapper extends RuntimeException {

    public NotRuntimeExceptionWrapper(Throwable cause) {
        super(cause);
        if (cause instanceof RuntimeException) {
            throw new IllegalArgumentException("Can't wrap RuntimeException");
        }
    }
}
