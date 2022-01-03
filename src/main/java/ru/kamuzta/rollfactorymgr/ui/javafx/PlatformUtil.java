package ru.kamuzta.rollfactorymgr.ui.javafx;

import javafx.application.Platform;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.kamuzta.rollfactorymgr.exception.ExceptionalRunnable;
import ru.kamuzta.rollfactorymgr.exception.ExceptionalSupplier;
import ru.kamuzta.rollfactorymgr.exception.Executor;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class PlatformUtil {

    /**
     * Execute callable.call() в JavaFX-Thread
     *
     * @param callable operation
     * @param <T>      result Type
     * @return result of executing callable.call()
     * can throw exceptions
     */
    @SneakyThrows
    public static <T> T executeInJavaFxThread(Callable<T> callable) {
        return executeInJavaFxThread1((ExceptionalSupplier<T>) callable::call);
    }

    @SneakyThrows
    public static void executeInJavaFxThreadSilent(ExceptionalRunnable runnable) {
        executeInJavaFxThread1((Executor) runnable::exec);
    }

    public static void executeInJavaFxThread(ExceptionalRunnable runnable) throws Exception {
        executeInJavaFxThread1((Executor) runnable::exec);
    }

    private static <T> T executeInJavaFxThread1(Supplier<T> supplier) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return supplier.get();
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Exception> exception = new AtomicReference<>();

        try {
            Platform.runLater(() -> {
                try {
                    result.set(supplier.get());
                } catch (Exception e) {
                    exception.set(e);
                } finally {
                    latch.countDown();
                }
            });

            latch.await();
        } catch (InterruptedException e) {
            log.error("Error while running with CountDownLatch", e);
        }

        if (exception.get() != null) {
            throw exception.get();
        }

        return result.get();
    }
}
