package ru.kamuzta.rollfactorymgr.ui.lock;

import org.jetbrains.annotations.NotNull;

public class CouldNotReleaseLockException  extends Exception{
    CouldNotReleaseLockException(@NotNull Throwable cause) {
        super(cause);
    }
}
