package ru.kamuzta.rollfactorymgr.ui.dialog;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.utils.exception.ExceptionUtils;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Task class for use in wait dialogs
 *
 * @param <T> result Type
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Slf4j
public abstract class AbstractWaitDialogTask<T> extends FutureTask<T> {

    private enum ResultState {
        SUCCEEDED,
        FAILED
    }

    private volatile Throwable exception;
    private volatile T value;
    private volatile ResultState resultState;
    private volatile Optional<Consumer<Throwable>> onFailed = Optional.empty();
    private volatile Optional<Consumer<T>> onSucceeded = Optional.empty();
    private volatile Optional<Consumer<Object>> onDone = Optional.empty();

    private final ReentrantLock lock = new ReentrantLock();

    public AbstractWaitDialogTask(Callable<T> callable) {
        super(callable);
    }

    @Override
    public void run() {
        try {
            lock.lock();
            super.run();
            value = get();
            setResultState(ResultState.SUCCEEDED);
            onSucceeded.ifPresent(consumer -> Platform.runLater(() -> consumer.accept(value)));
        } catch (Exception e) {
            log.error("Error while running task", e);
            exception = unwrapExecutionException(e);
            setResultState(ResultState.FAILED);
            onFailed.ifPresent(consumer -> Platform.runLater(() -> consumer.accept(exception)));
        } finally {
            onDone.ifPresent(consumer -> Platform.runLater(() -> consumer.accept(getException() != null ? getException() : getValue())));
            lock.unlock();
        }
    }

    /**
     * @return whether the task completed successfully, i.e. The operation was completed successfully, without exceptions, and value was set (even though null)
     */
    public boolean isSucceeded() {
        return resultState == ResultState.SUCCEEDED;
    }

    /**
     * @return the result of an already performed operation; if the operation was not performed or an exception occurred during execution, then null.
     */
    public T getValue() {
        return value;
    }

    /**
     * @return whether the task ended unsuccessfully, i.e. The operation was executed with exceptions and an exception was thrown
     */
    public boolean isFailed() {
        return resultState == ResultState.FAILED;
    }

    /**
     * @return the exception thrown during the execution of the operation; if the operation was not performed or no exceptions occurred during execution, then null.
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * @param onFailed handler for throwing exception when performing operation
     */
    public void setOnFailed(Consumer<Throwable> onFailed) {
        this.onFailed = Optional.ofNullable(onFailed);
    }

    /**
     * @param onSucceeded handler for the result of the operation
     */
    public void setOnSucceeded(Consumer<T> onSucceeded) {
        this.onSucceeded = Optional.of(onSucceeded);
    }

    /**
     * @param onDone handler for the result of the operation or the exception thrown when the operation was performed
     */
    public void setOnDone(Consumer<Object> onDone) {
        this.onDone = Optional.ofNullable(onDone);
    }

    public Consumer<Throwable> getOnFailed() {
        return onFailed.orElse(null);
    }

    public Consumer<T> getOnSucceeded() {
        return onSucceeded.orElse(null);
    }

    public Consumer<Object> getOnDone() {
        return onDone.orElse(null);
    }

    @Override
    public boolean isDone() {
        boolean result = super.isDone();
        return result && getDoneWithResultSafety(true);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return getDoneWithResultSafety(super.get());
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return getDoneWithResultSafety(super.get(timeout, unit));
    }

    private <A> A getDoneWithResultSafety(A result) {
        if (resultState == null) {
            // We are waiting for the task status to be processed.
            try {
                lock.lock();
                return result;
            } finally {
                lock.unlock();
            }
        } else {
            return result;
        }
    }

    private Throwable unwrapExecutionException(Throwable throwable) {
        if (throwable instanceof ExecutionException) {
            return throwable.getCause();
        }
        return throwable;
    }

    public static class RethrowExceptionOnFailed implements Consumer<Throwable> {
        @Override
        public void accept(Throwable throwable) {
            throw ExceptionUtils.wrapNotRuntimeException(throwable);
        }
    }

    public static class DoNothingOnFailed implements Consumer<Throwable> {
        @Override
        public void accept(Throwable throwable) { }
    }

    private ResultState getResultState() {
        return resultState;
    }

    private void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }
}
