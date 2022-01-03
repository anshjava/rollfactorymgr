package ru.kamuzta.rollfactorymgr.ui.keybinding;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.SimpleBooleanProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An action that can be skipped by a condition
 */
public class KeyBoundAction implements Runnable {

    private final Runnable action;
    private final BooleanExpression enabled;

    public KeyBoundAction(Runnable action, BooleanExpression enabled) {
        checkNotNull(action, "action is null");
        checkNotNull(enabled, "enabled is null");

        this.action = action;
        this.enabled = enabled;
    }

    public KeyBoundAction(Runnable action) {
        this(action, new SimpleBooleanProperty(true));
    }

    @Override
    public void run() {
        if (enabled.get()) {
            action.run();
        }
    }

    // ===============================================================================================================

    public Boolean isEnabled() {
        return enabled.get();
    }
}
