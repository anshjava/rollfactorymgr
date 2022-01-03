package ru.kamuzta.rollfactorymgr.event;

import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event of hotkey pressed
 */
public class KeyBindingPressedEvent extends UIEvent {
    private final KeyEvent keyEvent;

    public KeyBindingPressedEvent(@NotNull KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
    }

    @NotNull
    public KeyEvent getKeyEvent() {
        return keyEvent;
    }
}
