package ru.kamuzta.rollfactorymgr.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class UserNotificationsLog {

    public static Logger log() {
        return UserNotificationsLog.log;
    }

    public static void logErrorDialog(String title, String message) {
        log.info("Error Dialog: {}, details: {}", title, message);
    }

    public static void logMessageDialog(String title, String message) {
        log.info("Message Dialog: {}, details: {}", title, message);
    }

    public static void logMessageDialog(String title) {
        log.info("Message Dialog: {}", title);
    }

    public static void logNotification(String title) {
        log.info("User Notification: {}", title);
    }

    public static void logPrompt(String message) {
        log.info("Prompt: {}", message);
    }

    public static void logViewError(String message) {
        log.info("View Error: {}", message);
    }
}
