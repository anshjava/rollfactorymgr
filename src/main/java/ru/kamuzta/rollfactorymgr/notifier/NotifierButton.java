package ru.kamuzta.rollfactorymgr.notifier;

import lombok.Getter;

/**
 * Button for Notifier Screen
 */
@Getter
public class NotifierButton {

    private Purpose purpose;
    private String text;
    private Runnable action;
    private String afterActionMessage;

    public NotifierButton(Purpose purpose, String text, Runnable action) {
        this(purpose, text, action, null);
    }

    public NotifierButton(Purpose purpose, String text, Runnable action, String afterActionMessage) {
        this.purpose = purpose;
        this.text = text;
        this.action = action;
        this.afterActionMessage = afterActionMessage;
    }

    public enum Purpose {
        YES, NO, CANCEL,
    }
}
