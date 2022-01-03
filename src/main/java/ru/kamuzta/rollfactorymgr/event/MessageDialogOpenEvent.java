package ru.kamuzta.rollfactorymgr.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.ui.dialog.DialogAlert;
import ru.kamuzta.rollfactorymgr.ui.dialog.MessageDialogAction;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class MessageDialogOpenEvent extends UIEvent {
    private String title;
    private String message;
    private MessageDialogAction choiceOneAction;
    private Optional<MessageDialogAction> choiceTwoActionHolder;
    private DialogAlert dialog;
    private boolean important;
    private String hyperLinkText;

    public MessageDialogOpenEvent(String title, String message, MessageDialogAction choiceOneAction, @Nullable MessageDialogAction choiceTwoAction) {
        this(title, message, choiceOneAction, choiceTwoAction, null, false);
    }

    public MessageDialogOpenEvent(String title, String message, MessageDialogAction choiceOneAction, @Nullable MessageDialogAction choiceTwoAction, DialogAlert dialog) {
        this(title, message, choiceOneAction, choiceTwoAction, dialog, false);
    }

    public MessageDialogOpenEvent(String title, String message, MessageDialogAction choiceOneAction, @Nullable MessageDialogAction choiceTwoAction, DialogAlert dialog, boolean important) {
        this.title = title;
        this.message = message;
        this.choiceOneAction = choiceOneAction;
        this.important = important;
        this.choiceTwoActionHolder = Optional.ofNullable(choiceTwoAction);
        this.dialog = dialog;
    }
}
