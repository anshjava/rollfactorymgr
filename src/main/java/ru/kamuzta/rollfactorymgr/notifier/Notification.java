package ru.kamuzta.rollfactorymgr.notifier;

public enum Notification {

    RECEIVING_DATA("receivingData"),
    INTERRUPTING("interrupting");

    private final String text;

    Notification(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
