package ru.kamuzta.rollfactorymgr.ui.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.text.MessageFormat;

@Slf4j
class WindowsApplicationLock implements ApplicationLock {
    private static final File LOCK_FILE_PATH = new File(System.getProperty("java.io.tmpdir"), "shop-lite.pid");

    private static RandomAccessFile accessFile;
    private static FileChannel channel;
    private static FileLock lock;

    @Override
    public void enter() throws AlreadyLockedException, CouldNotLockException {
        if (LOCK_FILE_PATH.exists()) {
            log.trace("File {} exists", LOCK_FILE_PATH.getAbsolutePath());
            try {
                FileUtils.forceDelete(LOCK_FILE_PATH);
                log.trace("Deleted successfully.");
            } catch (IOException e) {
                log.trace("Error while deleting", e);
                throw new AlreadyLockedException();
            }
        }

        try {
            log.trace("Creating file {}", LOCK_FILE_PATH.getAbsolutePath());

            final boolean isCreated = LOCK_FILE_PATH.createNewFile();
            if (isCreated) {
                log.trace("Created successfully.");
            } else {
                throw new CouldNotLockException(MessageFormat.format("Could not create file {0}", LOCK_FILE_PATH.getAbsolutePath()));
            }
        } catch (IOException e) {
            log.trace("Error while creating lock file.", e);
            throw new CouldNotLockException(e);
        }

        try {
            final String jvmRunningName = ManagementFactory.getRuntimeMXBean().getName();
            try (BufferedWriter writer = Files.newBufferedWriter(LOCK_FILE_PATH.toPath())) {
                writer.write(jvmRunningName);
            }
            accessFile = new RandomAccessFile(LOCK_FILE_PATH, "rw");
            channel = accessFile.getChannel();
            lock = channel.tryLock();
        } catch (IOException e) {
            log.trace("Error while writing lock file.", e);
            throw new CouldNotLockException(e);
        }
    }

    @Override
    public void release() throws CouldNotReleaseLockException {
        log.trace("Releasing lock");
        try {
            lock.release();
            channel.close();
            accessFile.close();
            log.trace("Successfully");
        } catch (IOException e) {
            log.trace("Error while releasing", e);
            throw new CouldNotReleaseLockException(e);
        } finally {
            try {
                FileUtils.forceDelete(LOCK_FILE_PATH);
                log.trace("Force deleted succeeded");
            } catch (IOException e) {
                log.trace("Force deleted failed", e);
            }
        }
    }
}
