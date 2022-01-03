package ru.kamuzta.rollfactorymgr.ui.lock;

import com.google.inject.ProvidedBy;

@ProvidedBy(ApplicationLockProvider.class)
public interface ApplicationLock {
    void enter() throws AlreadyLockedException, CouldNotLockException;

    void release() throws CouldNotReleaseLockException;
}
