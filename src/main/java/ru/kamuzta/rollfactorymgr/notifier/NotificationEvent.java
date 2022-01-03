package ru.kamuzta.rollfactorymgr.notifier;

import lombok.Getter;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * An event, for notification of operations, carries a message and a timeout.
 */
public class NotificationEvent extends EventObject {
    public static final int NO_INTERRUPT_POSSIBLE = -1;

    protected final String message;
    protected final int timeout;

    protected final int interruptPeriod;
    /**
     * Event flag for interrupt service InterruptingService
     */
    @Getter
    protected final boolean interruptible;

    protected Runnable onTimeout;

    protected List<NotifierButton> buttons;

    /**
     * Constructs a prototypical Event.
     *
     * @param source          The object on which the Event initially occurred.
     * @param message         - operation message.
     * @param timeout         - timeout for the operation.
     * @param interruptPeriod - The number of seconds after which user interruption becomes available.
     * @throws IllegalArgumentException if source is null.
     */
    public NotificationEvent(Object source, String message, int timeout, int interruptPeriod, List<NotifierButton> buttons, Runnable onTimeout, boolean interruptible) {
        super(source);
        this.message = message;
        this.timeout = timeout;
        this.interruptPeriod = interruptPeriod;
        this.buttons = buttons;
        this.onTimeout = onTimeout;
        this.interruptible = interruptible;
    }

    public NotificationEvent(Object source, String message, int timeout, int interruptPeriod) {
        this(source, message, timeout, NO_INTERRUPT_POSSIBLE, new ArrayList<>(), null, false);
    }


    public NotificationEvent(Object source, String message, int timeout) {
        this(source, message, timeout, NO_INTERRUPT_POSSIBLE);
    }

    public NotificationEvent(Object source, String message) {
        this(source, message, 0, NO_INTERRUPT_POSSIBLE);
    }


    public String getMessage() {
        return message;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getInterruptPeriod() {
        return interruptPeriod;
    }

    public List<NotifierButton> getButtons() {
        return buttons;
    }

    public Runnable getOnTimeout() {
        return onTimeout;
    }

    public void setOnTimeout(Runnable onTimeout) {
        this.onTimeout = onTimeout;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
               "message=" + message +
               ", timeout=" + timeout +
               ", interruptPeriod=" + interruptPeriod +
               '}';
    }

    /**
     * Creates a new instance, replaces message
     *
     * @param newMessage
     * @return
     */
    public NotificationEvent copy(String newMessage) {
        return new NotificationEvent(this.source, newMessage, this.timeout, this.interruptPeriod, this.buttons, this.onTimeout, this.interruptible);
    }
}
