package ru.kamuzta.rollfactorymgr.event;

import lombok.Getter;
import ru.kamuzta.rollfactorymgr.notifier.Notifier;
import ru.kamuzta.rollfactorymgr.ui.dialog.AbstractWaitDialogTask;
import ru.kamuzta.rollfactorymgr.ui.dialog.DialogAlert;

import java.util.Optional;

@Getter
public class WaitDialogOpenEvent extends UIEvent {
    private final AbstractWaitDialogTask task;
    private final Optional<Notifier> notifiableHolder;
    private final String message;
    private DialogAlert dialog;

    public WaitDialogOpenEvent(AbstractWaitDialogTask task, Notifier notifier, String message, DialogAlert dialog) {
        this.task = task;
        this.notifiableHolder = Optional.ofNullable(notifier);
        this.message = message;
        this.dialog = dialog;
    }

    public WaitDialogOpenEvent(AbstractWaitDialogTask task, Notifier notifier, String message) {
        this(task, notifier, message, null);
    }
}
