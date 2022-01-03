package ru.kamuzta.rollfactorymgr.service.async;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Singleton
public class AsynchronousServiceImpl implements AsynchronousService {
    private final Map<Class<?>, ExecutorService> executionCache = new ConcurrentHashMap<>();
    private final Object monitor = new Object();

    @Inject
    public AsynchronousServiceImpl() {
        createCachedExecutor(Object.class);
    }

    @Override
    public Future<?> runAsync(Runnable task) {
        return runAsync(Object.class, task);
    }

    @Override
    public <V> Future<V> runAsync(Callable<V> task) {
        return runAsync(Object.class, task);
    }

    @Override
    public Future<?> runAsync(Class<?> clazz, Runnable task) {
        log.info("Adding async task by locker {}", clazz);
        return getExecutor(clazz).submit(task);
    }

    @Override
    public <V> Future<V> runAsync(Class<?> clazz, Callable<V> task) {
        log.info("Adding async task by locker {}", clazz);
        return getExecutor(clazz).submit(task);
    }

    @Override
    public void shutdownAll() {
        synchronized (monitor) {
            this.executionCache.values().forEach(ExecutorService::shutdown);
            this.executionCache.clear();
        }
    }

    private ExecutorService getExecutor(Class<?> clazz) {
        ExecutorService executorService = this.executionCache.get(clazz);
        if (executorService == null) {
            synchronized (monitor) {
                executorService = this.executionCache.get(clazz);
                if (executorService == null) {
                    executorService = createCachedExecutor(clazz);
                }
            }
        }
        return executorService;
    }

    private ExecutorService createCachedExecutor(Class<?> clazz) {
        synchronized (monitor) {
            ExecutorService executorService = this.executionCache.get(clazz);
            if (executorService != null) {
                executorService.shutdown();
            }
            this.executionCache.put(clazz, Executors.newSingleThreadExecutor());
            return this.executionCache.get(clazz);
        }
    }
}
