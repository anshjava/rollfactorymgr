package ru.kamuzta.rollfactorymgr.notifier;

/**
 * In a general sense, an extension for notifying the user about service events etc.
 *
 */
public interface Notifier {

    /**
     * Add notification listener
     */
    void addNotificationListener(NotificationListener listener);

    /**
     * Remove notification listener
     */
    void removeNotificationListener(NotificationListener listener);

    /**
     * Remove all notification listeners
     */
    void removeAllListeners();

    /**
     * Interrupt operation
     */
    void interrupt();
}
