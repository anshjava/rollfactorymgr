package ru.kamuzta.rollfactorymgr.event;

import javafx.scene.Node;
import javafx.scene.input.KeyCodeCombination;
import lombok.Getter;

/**
 * Event of removing action with hotkey
 */
@Getter
public class KeyBindingRemoveEvent extends UIEvent {
    private final Node node;
    private final KeyCodeCombination keyBinding; // хоткей
    private final boolean common; // хоткей для всех экранов ли?

    /**
     * @param node       interface element which has hotkey
     * @param keyBinding hotkey
     * @param common     hotkey for all screens?
     */
    public KeyBindingRemoveEvent(Node node, KeyCodeCombination keyBinding,
                                 boolean common) {
        this.node = node;
        this.keyBinding = keyBinding;
        this.common = common;
    }
}

