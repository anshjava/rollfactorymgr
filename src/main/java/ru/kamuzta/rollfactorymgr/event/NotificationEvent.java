package ru.kamuzta.rollfactorymgr.event;

import lombok.Getter;

@Getter
public class NotificationEvent extends UIEvent {

    private final String text;

    public NotificationEvent(String text) {
        this.text = text;
    }
}
