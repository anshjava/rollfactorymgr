package ru.kamuzta.rollfactorymgr.ui.dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCodeCombination;
import lombok.Builder;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.exception.ExceptionalRunnable;

@Getter
@Builder
public class MessageDialogAction {
    private final String name;
    private final ExceptionalRunnable operation;
    private final ButtonType buttonType;
    private final KeyCodeCombination keyBinding;
    private boolean disabled;
}
