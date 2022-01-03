package ru.kamuzta.rollfactorymgr.service.interrupt;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.notifier.NotificationEvent;
import ru.kamuzta.rollfactorymgr.ui.ValueChangeListener;

/**
 * Service for interrupting the execution of current operations from the outside
 * <p>
 * It is assumed that the interrupt occurs immediately for all active operations added to the service
 */
@ImplementedBy(InterruptingServiceImpl.class)
public interface InterruptingService {

    /**
     * Checks if the service has registered operations to interrupt
     */
    boolean hasActiveInterruptActions();

    /**
     * Adds a listener to update the status of having active operations to interrupt
     */
    void addHasActiveInterruptListener(@NotNull ValueChangeListener<Boolean> listener);

    /**
     * Removes the listener to update the status of having active operations to interrupt
     *
     * @return {@code true}, if listener has been found and removed.
     */
    boolean removeListener(@NotNull ValueChangeListener<Boolean> listener);

    /**
     * Adds an action to interrupt. When executed, the action is removed from the service
     * (i.e. each individual action is performed in one card)
     */
    void registerInterruptAction(@NotNull Runnable interruptAction);

    /**
     * Removes the interrupt action. Use when the current action is no longer relevant (no longer feasible).
     *
     * @return {@code true}, если такое действие найдено и удалено.
     */
    boolean removeInterruptAction(@NotNull Runnable interruptAction);

    /**
     * Indicates the need to interrupt all operations.
     * Causes all registered interrupt actions to be executed.
     * After a single execution, the actions are removed from the service, since the interrupt is considered completed.
     */
    void fireInterrupt();

    NotificationEvent createNotificationEvent(String message, String buttonText, String afterInterruptMessage);
}