package ru.kamuzta.rollfactorymgr.utils.json;

import ru.kamuzta.rollfactorymgr.exception.UserFriendlyException;

public class CouldNotSeserializeToJsonException extends UserFriendlyException {

    public CouldNotSeserializeToJsonException() {
        super();
    }

    public CouldNotSeserializeToJsonException(String message) {
        super(message);
    }

    public CouldNotSeserializeToJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotSeserializeToJsonException(Throwable cause) {
        super(cause);
    }
}
