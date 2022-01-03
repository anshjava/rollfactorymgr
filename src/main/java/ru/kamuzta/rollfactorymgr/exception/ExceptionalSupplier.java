package ru.kamuzta.rollfactorymgr.exception;

import ru.kamuzta.rollfactorymgr.utils.exception.ExceptionUtils;

import java.util.function.Supplier;

@FunctionalInterface
public interface ExceptionalSupplier<T> extends Supplier<T> {

    default T get() {
        try {
            return getIt();
        } catch (Exception e) {
            throw ExceptionUtils.wrapNotRuntimeException(e);
        }
    }

    T getIt() throws Exception;
}
