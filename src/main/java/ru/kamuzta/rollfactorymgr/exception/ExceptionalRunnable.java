package ru.kamuzta.rollfactorymgr.exception;

import ru.kamuzta.rollfactorymgr.utils.exception.ExceptionUtils;

@FunctionalInterface
public interface ExceptionalRunnable extends Runnable {

    default void run() {
        try {
            exec();
        } catch (Exception e) {
            throw ExceptionUtils.wrapNotRuntimeException(e);
        }
    }

    void exec() throws Exception;
}
