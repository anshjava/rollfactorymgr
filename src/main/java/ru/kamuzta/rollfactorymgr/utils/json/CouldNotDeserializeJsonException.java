package ru.kamuzta.rollfactorymgr.utils.json;

import ru.kamuzta.rollfactorymgr.exception.UserFriendlyException;

public class CouldNotDeserializeJsonException extends UserFriendlyException {

    public CouldNotDeserializeJsonException() {
        super();
    }

    public CouldNotDeserializeJsonException(String message) {
        super(message);
    }

    public CouldNotDeserializeJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotDeserializeJsonException(Throwable cause) {
        super(cause);
    }
}
