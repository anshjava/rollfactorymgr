package ru.kamuzta.rollfactorymgr.event;

import ru.kamuzta.rollfactorymgr.exception.ExceptionalRunnable;
import ru.kamuzta.rollfactorymgr.ui.dialog.AbstractWaitDialogTask;

public class WaitDialogTaskWithoutResult extends AbstractWaitDialogTask<Void> {

    public WaitDialogTaskWithoutResult(ExceptionalRunnable operation) {
        super(() -> {
            operation.run();
            return null;
        });
    }
}
