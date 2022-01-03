package ru.kamuzta.rollfactorymgr.exception;

@FunctionalInterface
public interface Executor extends ExceptionalSupplier<Void> {

    default Void getIt() throws Exception {
        exec();
        return null;
    }

    void exec() throws Exception;
}
