package ru.kamuzta.rollfactorymgr.ui.lock;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

class ApplicationLockProvider implements Provider<ApplicationLock> {
    private final WindowsApplicationLock windowsApplicationLock;
    private final LinuxApplicationLock linuxApplicationLock;

    @Inject
    ApplicationLockProvider(@NotNull WindowsApplicationLock windowsApplicationLock, @NotNull LinuxApplicationLock linuxApplicationLock) {
        this.windowsApplicationLock = windowsApplicationLock;
        this.linuxApplicationLock = linuxApplicationLock;
    }

    @Override
    public ApplicationLock get() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return windowsApplicationLock;
        } else if (SystemUtils.IS_OS_LINUX) {
            return linuxApplicationLock;
        } else {
            throw new UnsupportedOperationException("Unsupported operation system: " + SystemUtils.OS_NAME);
        }
    }
}
