package ru.kamuzta.rollfactorymgr.notifier;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Events Notifier
 *
 */
@Slf4j
public abstract class AbstractNotifier implements Notifier {
    public static final int DEFAULT_INTERRUPT_DELAY = 10;

    protected final List<NotificationListener> listeners = new CopyOnWriteArrayList<NotificationListener>();

    @Override
    public void addNotificationListener(NotificationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        listeners.clear();
    }

    /**
     * @param operation - operation in progress
     * @param timeout   - timeout for operation
     * @param interrupt - time in seconds after that operation interruption by user is allowed. If value is negative - interruption is not allowed
     */
    protected void fireNotified(Notification operation, int timeout, int interrupt) {
        NotificationEvent event = new NotificationEvent(this, operation.getText(), timeout, interrupt);
        log.info("Fired notification {}", operation);
        log.info("   Timeout is {}, interruptPeriod is {}", timeout, interrupt);
        for (NotificationListener listener : listeners) {
            listener.notified(event);
        }
    }

    protected void fireNotified(String message, int timeout, int interrupt) {
        NotificationEvent event = new NotificationEvent(this, message, timeout, interrupt);
        for (NotificationListener listener : listeners) {
            listener.notified(event);
        }
    }

    protected void fireNotified(String message, int timeout) {
        fireNotified(message, timeout, NotificationEvent.NO_INTERRUPT_POSSIBLE);
    }

    protected void fireNotified(Notification operation, int timeout) {
        fireNotified(operation, timeout, NotificationEvent.NO_INTERRUPT_POSSIBLE);
    }

    protected void fireNotified(Notification operation) {
        fireNotified(operation, 0, NotificationEvent.NO_INTERRUPT_POSSIBLE);
    }

    protected void fireNotified(String message) {
        fireNotified(message, 0, NotificationEvent.NO_INTERRUPT_POSSIBLE);
    }

    protected void fireNotified(NotificationEvent event) {
        for (NotificationListener listener : listeners) {
            listener.notified(event);
        }
    }

    public void fireNotifiedP(String message) {
        this.fireNotified(message);
    }

    public void fireNotifiedP(NotificationEvent event) {
        for (NotificationListener listener : listeners) {
            listener.notified(event);
        }
    }

    @Override
    public void interrupt() {
        log.warn("Request to interrupt operation received");
        fireNotified(Notification.INTERRUPTING);
        doInterrupt();
    }

    protected void doInterrupt() {
        //TODO
    }
}
