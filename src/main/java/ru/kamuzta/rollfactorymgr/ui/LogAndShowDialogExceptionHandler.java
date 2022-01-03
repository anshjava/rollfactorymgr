package ru.kamuzta.rollfactorymgr.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.kamuzta.rollfactorymgr.ui.dialog.DialogHelper;


/**
 * Generic handler for all unhandled exceptions
 * Displays a message in the log and sends an event to open the error dialog
 */
@Slf4j
@Singleton
public class LogAndShowDialogExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final DialogHelper dialogHelper;
    private final EventBus eventBus;

    @Inject
    LogAndShowDialogExceptionHandler(DialogHelper dialogHelper, EventBus eventBus) {
        this.dialogHelper = dialogHelper;
        this.eventBus = eventBus;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("Error", throwable);
        Platform.runLater(() -> dialogHelper.showError(throwable));
    }

}
