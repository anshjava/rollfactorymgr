package ru.kamuzta.rollfactorymgr.ui.lock;

import org.jetbrains.annotations.NotNull;

public class CouldNotLockException extends Exception {
    CouldNotLockException(@NotNull Throwable cause) {
        super(cause);
    }

    CouldNotLockException(String message) {
        super(message);
    }
}
