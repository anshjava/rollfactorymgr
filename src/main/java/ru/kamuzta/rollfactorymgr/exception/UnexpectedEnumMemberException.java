package ru.kamuzta.rollfactorymgr.exception;

import java.text.MessageFormat;

public class UnexpectedEnumMemberException extends UserFriendlyException {
    public UnexpectedEnumMemberException(Enum e) {
        super(MessageFormat.format("Unexpected member value of Enumeration {0}: {1}. Please contact your support service.",
                e.getClass().getCanonicalName(), e));
    }
}
