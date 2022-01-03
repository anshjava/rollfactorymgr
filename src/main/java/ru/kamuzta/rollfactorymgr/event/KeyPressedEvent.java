package ru.kamuzta.rollfactorymgr.event;

import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;

public class KeyPressedEvent extends UIEvent {
    private final KeyEvent keyEvent;

    public KeyPressedEvent(@NotNull KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
    }

    @NotNull
    public KeyEvent getKeyEvent() {
        return keyEvent;
    }
}
