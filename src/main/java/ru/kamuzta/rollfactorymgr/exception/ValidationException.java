package ru.kamuzta.rollfactorymgr.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ValidationException extends UserFriendlyException {

    public ValidationException(String errorDescription) {
        super(errorDescription);
        log.warn("ValidationError: " + errorDescription);
    }

    public ValidationException(String errorDescription, Throwable e) {
        super(errorDescription, e);
        log.warn("ValidationError: " + errorDescription + "Exception cause: " + e);
    }

}
