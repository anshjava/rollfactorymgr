package ru.kamuzta.rollfactorymgr.event;

import javafx.scene.Node;
import javafx.scene.input.KeyCodeCombination;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.ui.keybinding.KeyBoundAction;

/**
 * Event of Action with Bound HotKey on Node
 */
@Getter
public class KeyBindingCreateEvent extends UIEvent {
    private final Node node;
    private final KeyCodeCombination keyBinding;
    private final KeyBoundAction action;
    private final boolean common;

    /**
     * @param node       interface element which has hotkey
     * @param keyBinding hotkey
     * @param action     hotkey action
     * @param common     hotkey for all screens?
     */
    public KeyBindingCreateEvent(Node node,
                                 KeyCodeCombination keyBinding,
                                 KeyBoundAction action,
                                 boolean common) {
        this.node = node;
        this.keyBinding = keyBinding;
        this.action = action;
        this.common = common;
    }

}

