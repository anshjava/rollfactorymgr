package ru.kamuzta.rollfactorymgr.ui;

import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCodeCombination;
import ru.kamuzta.rollfactorymgr.ui.keybinding.KeyBindingUtil;
import ru.kamuzta.rollfactorymgr.ui.keybinding.KeyBindingsTumbler;
import ru.kamuzta.rollfactorymgr.ui.keybinding.KeyBoundAction;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static ru.kamuzta.rollfactorymgr.ui.KeyBindingActiveCondition.*;
import static javafx.beans.binding.Bindings.not;

/**
 * A button with a hotkey attached to it.
 */
@DefaultProperty(value = "keyBinding")
public class KeyBoundButton extends ButtonLogged implements KeyBindingsTumbler {

    private ObjectProperty<KeyCodeCombination> keyBinding = new SimpleObjectProperty<>();
    private BooleanProperty keyBindingLabelVisible = new SimpleBooleanProperty(true);
    private BooleanProperty commonKeyBinding = new SimpleBooleanProperty(false);

    private BooleanProperty turnedOn = new SimpleBooleanProperty(true);
    private BooleanProperty storedKeyBindingLabelVisible = new SimpleBooleanProperty(true);

    public KeyBoundButton() {
        keyBinding.addListener((observable, oldKeyBinding, newKeyBinding) -> {
            if (!keyBindingActivityChanged(KEY_BINDING_NOT_NULL)) {
                return;
            }

            fireRemoveKeyBindingEvent(oldKeyBinding, getCommonKeyBinding());
            fireCreateKeyBindingEvent();
        });

        commonKeyBinding.addListener((observable, oldCommonKeyBinding, newCommonKeyBinding) -> {
            if (!keyBindingActivityChanged(null)) {
                return;
            }

            fireRemoveKeyBindingEvent(getKeyBinding(), oldCommonKeyBinding);
            fireCreateKeyBindingEvent();
        });

        turnedOn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setKeyBindingLabelVisible(storedKeyBindingLabelVisible.getValue());
            } else {
                storedKeyBindingLabelVisible.setValue(getKeyBindingLabelVisible());
                setKeyBindingLabelVisible(false);
            }

            if (!keyBindingActivityChanged(TURNED_ON)) {
                return;
            }

            if (newValue) {
                fireCreateKeyBindingEvent();
            } else {
                fireRemoveKeyBindingEvent(getKeyBinding(), getCommonKeyBinding());
            }
        });

        disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (!keyBindingActivityChanged(NOT_DISABLED)) {
                return;
            }

            if (newValue) {
                fireRemoveKeyBindingEvent(getKeyBinding(), getCommonKeyBinding());
            } else {
                fireCreateKeyBindingEvent();
            }
        });

        visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!keyBindingActivityChanged(VISIBLE)) {
                return;
            }

            if (newValue) {
                fireCreateKeyBindingEvent();
            } else {
                fireRemoveKeyBindingEvent(getKeyBinding(), getCommonKeyBinding());
            }
        });

        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (!keyBindingActivityChanged(SCENE_NOT_NULL)) {
                return;
            }

            if (newScene != null) {
                fireCreateKeyBindingEvent();
            } else {
                fireRemoveKeyBindingEvent(getKeyBinding(), getCommonKeyBinding());
            }
        });
    }

    @Override
    public void turnOnKeyBindings() {
        turnedOn.set(true);
    }

    @Override
    public void turnOffKeyBindings() {
        turnedOn.set(false);
    }

    private void fireCreateKeyBindingEvent() {
        //todo

    }

    private void fireRemoveKeyBindingEvent(KeyCodeCombination keyBinding, Boolean commonBinding) {
        //todo
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new KeyBoundButtonSkin(this);
    }

    protected KeyBoundButtonSkin getKeyBoundButtonSkin() {
        return (KeyBoundButtonSkin) getSkin();
    }


    private ObservableBooleanValue conditionToObservableValue(KeyBindingActiveCondition condition) {
        switch (condition) {
            case KEY_BINDING_NOT_NULL:
                return keyBinding.isNotNull();
            case TURNED_ON:
                return turnedOn;
            case VISIBLE:
                return visibleProperty();
            case NOT_DISABLED:
                return not(disabledProperty());
            case SCENE_NOT_NULL:
                return sceneProperty().isNotNull();
        }

        return null;
    }

    public boolean keyBindingActivityChanged(KeyBindingActiveCondition changedPropertyCondition) {
        List<KeyBindingActiveCondition> activeStateProperties = newArrayList(KEY_BINDING_NOT_NULL, TURNED_ON, VISIBLE, NOT_DISABLED, SCENE_NOT_NULL);

        activeStateProperties.remove(changedPropertyCondition);

        return activeStateProperties.stream().map(this::conditionToObservableValue).map(ObservableBooleanValue::get).reduce(Boolean::logicalAnd).get();
    }


    // ===============================================================================================================
    public ObjectProperty<KeyCodeCombination> keyBindingProperty() {
        return keyBinding;
    }

    public KeyCodeCombination getKeyBinding() {
        return keyBinding.get();
    }

    public void setKeyBinding(KeyCodeCombination keyBinding) {
        this.keyBinding.set(keyBinding);
    }

    public boolean getKeyBindingLabelVisible() {
        return keyBindingLabelVisible.get();
    }

    public BooleanProperty keyBindingLabelVisibleProperty() {
        return keyBindingLabelVisible;
    }

    public void setKeyBindingLabelVisible(boolean keyBindingLabelVisible) {
        this.keyBindingLabelVisible.set(keyBindingLabelVisible);
    }

    public boolean getCommonKeyBinding() {
        return commonKeyBinding.get();
    }

    public BooleanProperty commonKeyBindingProperty() {
        return commonKeyBinding;
    }

    public void setCommonKeyBinding(boolean commonKeyBinding) {
        this.commonKeyBinding.set(commonKeyBinding);
    }

    public boolean getTurnedOn() {
        return turnedOn.get();
    }

    public BooleanProperty turnedOnProperty() {
        return turnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        this.turnedOn.set(turnedOn);
    }
}


