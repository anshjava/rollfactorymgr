package ru.kamuzta.rollfactorymgr.service.async;

import com.google.inject.ImplementedBy;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Asynchronous call service by application
 */
@ImplementedBy(AsynchronousServiceImpl.class)
public interface AsynchronousService {
    Future<?> runAsync(Runnable task);

    <V> Future<V> runAsync(Callable<V> task);

    Future<?> runAsync(Class<?> clazz, Runnable task);

    <V> Future<V> runAsync(Class<?> clazz, Callable<V> task);

    void shutdownAll();
}
