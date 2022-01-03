package ru.kamuzta.rollfactorymgr.ui.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;

@Slf4j
class LinuxApplicationLock implements ApplicationLock {
    private static final File LOCK_FILE_PATH = new File(System.getProperty("java.io.tmpdir"), "shop-lite.pid");
    private final Object lock = new Object();

    @Override
    public void enter() throws AlreadyLockedException, CouldNotLockException {
        synchronized (lock) {
            try {
                final String currentPID = getPID();

                if (LOCK_FILE_PATH.exists()) {
                    final String pidInFile = FileUtils.readFileToString(LOCK_FILE_PATH);

                    if (pidInFile.equals(currentPID)) {
                        log.warn("Lock is already set by current process.");
                        return;
                    } else {
                        final boolean isProcessRunning = checkIfProcessIsRunning(pidInFile);
                        if (isProcessRunning) {
                            throw new AlreadyLockedException();
                        }
                    }
                }

                FileUtils.writeStringToFile(LOCK_FILE_PATH, currentPID);
            } catch (IOException | CouldNotGetPIDException | InterruptedException e) {
                throw new CouldNotLockException(e);
            }
        }
    }

    @Override
    public void release() throws CouldNotReleaseLockException {
        synchronized (lock) {
            try {
                FileUtils.forceDelete(LOCK_FILE_PATH);
            } catch (IOException e) {
                throw new CouldNotReleaseLockException(e);
            }
        }
    }

    /**
     * <a href="http://stackoverflow.com/a/7690178">StackOverflow</a>.
     */
    private String getPID() throws CouldNotGetPIDException {
        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index < 1) {
            // part before '@' empty (index = 0) / '@' not found (index = -1)
            throw new CouldNotGetPIDException(MessageFormat.format("Could not get pid by jvmName {0}.", jvmName));
        }

        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        } catch (NumberFormatException e) {
            throw new CouldNotGetPIDException(MessageFormat.format("Could not parse pid from jvmName {0}.", jvmName));
        }
    }

    private boolean checkIfProcessIsRunning(@NotNull String pid) throws IOException, InterruptedException {
        final String command = MessageFormat.format("ps -p {0}", pid);
        final int result = Runtime.getRuntime().exec(command).waitFor();

        return result == 0; // process exists
    }

    private class CouldNotGetPIDException extends Exception {
        CouldNotGetPIDException(String message) {
            super(message);
        }
    }
}
