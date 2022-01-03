package ru.kamuzta.rollfactorymgr.ui;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import ru.kamuzta.rollfactorymgr.ui.keybinding.KeyBindingUtil;

public class KeyBoundButtonSkin extends ButtonSkin {

    private Label keyBindingLabel;

    public KeyBoundButtonSkin(KeyBoundButton button) {
        super(button);
        keyBindingLabel = new Label();
        keyBindingLabel.getStyleClass().add("key-binding-label");
        keyBindingLabel.textProperty().bind(Bindings.createStringBinding(() -> KeyBindingUtil.getDisplayTextImproved(button.getKeyBinding()),
                                                                         button.keyBindingProperty()));
        keyBindingLabel.visibleProperty().bind(button.keyBindingLabelVisibleProperty());
        getChildren().add(keyBindingLabel);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        layoutInArea(keyBindingLabel, x, y, w, h, -1, HPos.RIGHT, VPos.BOTTOM);
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();

        if (keyBindingLabel != null && !getChildren().contains(keyBindingLabel)) {
            getChildren().add(keyBindingLabel);
        }
    }
}
