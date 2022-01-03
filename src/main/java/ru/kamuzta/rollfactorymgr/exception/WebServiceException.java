package ru.kamuzta.rollfactorymgr.exception;

public class WebServiceException extends UserFriendlyException {

    RestException restException;

    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
        if (cause instanceof RestException) {
            restException = (RestException) cause;
        }
    }

    public RestException getRestException() {
        return restException;
    }
}
