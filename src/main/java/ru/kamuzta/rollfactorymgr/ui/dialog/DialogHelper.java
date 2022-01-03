package ru.kamuzta.rollfactorymgr.ui.dialog;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.event.ErrorDialogOpenEvent;
import ru.kamuzta.rollfactorymgr.event.MessageDialogOpenEvent;
import ru.kamuzta.rollfactorymgr.event.WaitDialogOpenEvent;
import ru.kamuzta.rollfactorymgr.exception.ExceptionalRunnable;
import ru.kamuzta.rollfactorymgr.notifier.Notifier;
import ru.kamuzta.rollfactorymgr.service.async.AsynchronousService;
import ru.kamuzta.rollfactorymgr.ui.main.MainView;
import ru.kamuzta.rollfactorymgr.ui.javafx.PlatformUtil;
import ru.kamuzta.rollfactorymgr.utils.exception.ExceptionUtils;
import ru.kamuzta.rollfactorymgr.utils.UserNotificationsLog;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
@Singleton
public class DialogHelper {

    public static final int MILLIS_BEFORE_SHOW_WAIT_DIALOG = 100;
    private static final KeyCodeCombination DEFAULT_OK_KEY_BINDING = new KeyCodeCombination(KeyCode.ENTER);
    private static final KeyCodeCombination DEFAULT_CANCEL_KEY_BINDING = new KeyCodeCombination(KeyCode.ESCAPE);

    private final EventBus mainEventBus;
    private final Provider<MainView> mainViewProvider;
    private final AsynchronousService asynchronousService;
    @Deprecated
    private boolean inWaitDialog = false;

    @Inject
    public DialogHelper(EventBus mainEventBus, Provider<MainView> mainViewProvider, AsynchronousService asynchronousService) {
        this.mainEventBus = mainEventBus;
        this.mainViewProvider = mainViewProvider;
        this.asynchronousService = asynchronousService;
    }

    /**
     * Runs a task in a separate thread and after some time, if the task has not been executed, sends an event to
     * open waiting dialog
     *
     * @param <T>      task return type
     * @param task     longtime operation.
     * @param message  initial message for waiting dialog
     * @param notifier a service that can be hooked to listen for events occurring in it to update the wait dialog
     */
    public <T> void executeLongtimeNotifiableOperation(AbstractWaitDialogTask<T> task, String message, Notifier notifier) {
        if (task.getOnFailed() == null) {
            task.setOnFailed(new AbstractWaitDialogTask.RethrowExceptionOnFailed());
        }

        Future<?> future = asynchronousService.runAsync(this.getClass(), task);
        try {
            Thread.sleep(MILLIS_BEFORE_SHOW_WAIT_DIALOG);
        } catch (InterruptedException e) {
            log.error("Error while sleeping", e);
        }

        if (!task.isDone()) {
            mainEventBus.post(new WaitDialogOpenEvent(task, notifier, message));
        }
    }

    public <T> void executeLongtimeOperation(AbstractWaitDialogTask<T> task, String message) {
        executeLongtimeNotifiableOperation(task, message, null);
    }

    public <T> void executeLongtimeNotifiableOperationAndWait(AbstractWaitDialogTask<T> task, String message, Notifier notifier) {
        log.warn(">>>  START: EXECUTE  " + message);
        if (!Platform.isFxApplicationThread()) {
            log.error("Not JFX Thread - so run task '{}'", message);
            task.run();
            handleIgnoredFailure(task);
            log.warn(">>> FINISH: EXECUTE " + message);
            return;
        }

        if (inWaitDialog) {
            log.error("In wait dialog already - so run task '{}'", message);
            task.run();
            handleIgnoredFailure(task);
            log.warn(">>>  FINISH: EXECUTE " + message);
            return;
        }

        try {
            inWaitDialog = true;
            Future<?> future = asynchronousService.runAsync(this.getClass(), task);
            try {
                future.get(MILLIS_BEFORE_SHOW_WAIT_DIALOG, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error while sleeping", e);
            } catch (TimeoutException ignore) {
            }

            if (!task.isDone()) {
                DialogAlert dialog = new DialogAlert();

                mainViewProvider.get().initWaitDialog(new WaitDialogOpenEvent(task, notifier, message, dialog));

                log.warn(">>>  START: SHOW & WAIT " + message);
                Optional<ButtonType> buttonType = dialog.showAndWait();
                log.warn(">>>  FINISH: SHOW & WAIT " + message);
            }
            handleIgnoredFailure(task);
        } finally {
            inWaitDialog = false;
        }

        log.warn(">>>  FINISH: EXECUTE " + message);
    }

    public <T> void executeLongtimeOperationAndWait(AbstractWaitDialogTask<T> task, String message) {
        executeLongtimeNotifiableOperationAndWait(task, message, null);
    }

    public void showError(Throwable throwable, String message) {
        log.error("Error dialog shown with cause: " + message, throwable);
        UserNotificationsLog.logErrorDialog("error", message);
        mainEventBus.post(new ErrorDialogOpenEvent(throwable, message));
    }

    public void showError(Throwable throwable) {
        showError(throwable, ExceptionUtils.createErrorMessage(ExceptionUtils.unwrapWrappedNotRuntimeException(throwable)));
    }

    /**
     * Sends an event to open a dialog with two alternative actions (in the simple confirmation case)
     *
     * @param title           dialog title
     * @param message         dialog message
     * @param choiceOneAction action performed by clicking on first button
     * @param choiceTwoAction action performed by clicking on second button, if null - there will be only one button
     */
    public void showMessageDialog(String title, String message, @NotNull MessageDialogAction choiceOneAction, @Nullable MessageDialogAction choiceTwoAction) {
        checkNotNull(choiceOneAction, "choiceOneAction is null");
        UserNotificationsLog.logMessageDialog(title, message);
        mainEventBus.post(new MessageDialogOpenEvent(title, message, choiceOneAction, choiceTwoAction));
    }

    /**
     * Sends an event to open an info dialog with an action after closing
     *
     * @param title    dialog title
     * @param message  dialog message
     * @param okAction action performed by clicking on button. if null - button will have default label
     */
    public void showInformation(String title, String message, @Nullable MessageDialogAction okAction) {
        showMessageDialog(title, message, okAction != null ? okAction : createDefaultOKAction(), null);
    }

    /**
     * Sends an event to open the info dialog
     *
     * @param title   dialog title
     * @param message dialog message
     */
    public void showInformation(String title, String message) {
        showInformation(title, message, null);
    }

    /**
     * Sends an event to open the info dialog without title
     *
     * @param message dialog message
     */
    public void showInformation(String message) {
        showInformation("", message, null);
    }

    /**
     * Sends an event to open a confirmation dialog
     *
     * @param title        dialog title
     * @param message      dialog message
     * @param okAction     action performed by clicking on button (OK)
     * @param cancelAction action performed by clicking on button (Cancel)
     */
    public void showConfirmation(String title, String message, @NotNull MessageDialogAction okAction, @NotNull MessageDialogAction cancelAction) {
        checkNotNull(cancelAction, "cancelAction is null");

        showMessageDialog(title, message, okAction, cancelAction);
    }

    public void showConfirmation(String title, String message, MessageDialogAction okAction) {
        showConfirmation(title, message, okAction, createDefaultCancelAction());
    }

    public ButtonType showMessageDialogAndWait(String title, String message, @NotNull MessageDialogAction choiceOneAction, @Nullable MessageDialogAction choiceTwoAction, boolean important) {
        UserNotificationsLog.logMessageDialog(title, message);
        return PlatformUtil.executeInJavaFxThread(() -> {
            checkNotNull(choiceOneAction, "choiceOneAction is null");
            DialogAlert dialog = new DialogAlert();
            mainViewProvider.get().initMessageDialog(new MessageDialogOpenEvent(title, message, choiceOneAction, choiceTwoAction, dialog, important));
            return dialog.showAndWait().get();
        });
    }

    public ButtonType showMessageDialogAndWait(String title, String message, @NotNull MessageDialogAction choiceOneAction, @Nullable MessageDialogAction choiceTwoAction, boolean important, String hyperLinkText) {
        UserNotificationsLog.logMessageDialog(title, message);
        return PlatformUtil.executeInJavaFxThread(() -> {
            checkNotNull(choiceOneAction, "choiceOneAction is null");

            DialogAlert dialog = new DialogAlert();

            mainViewProvider.get().initMessageDialog(new MessageDialogOpenEvent(title, message, choiceOneAction, Optional.ofNullable(choiceTwoAction), dialog, important, hyperLinkText));

            return dialog.showAndWait().get();
        });
    }

    public void showInformationAndWait(String title, String message, @Nullable MessageDialogAction okAction) {
        showMessageDialogAndWait(title, message, okAction != null ? okAction : createDefaultOKAction(), null, false);
    }

   public void showInformationAndWait(String title, String message, String hyperLinkText) {
        showMessageDialogAndWait(title, message, createDefaultOKAction(), null, false, hyperLinkText);
    }

    public void showInformationAndWait(String title, String message) {
        showInformationAndWait(title, message, (MessageDialogAction) null);
    }

    public ButtonType showConfirmationAndWait(String title, String message, @NotNull MessageDialogAction okAction, @NotNull MessageDialogAction cancelAction) {
        return showMessageDialogAndWait(title, message, okAction, cancelAction, false);
    }

    public ButtonType showConfirmationAndWait(String title, String message, @NotNull MessageDialogAction okAction) {
        return showConfirmationAndWait(title, message, okAction, createDefaultCancelAction());
    }

    public ButtonType showConfirmationAndWait(String title, String message) {
        return showConfirmationAndWait(title, message, createDefaultOKAction(), createDefaultCancelAction());
    }

    public ButtonType showImportantConfirmationAndWait(String title, String message) {
        return showMessageDialogAndWait(title, message, createDefaultOKAction(), createDefaultCancelAction(), true);
    }

    public ButtonType showConfirmationYesNoAndWait(String title, String message) {
        return showConfirmationAndWait(title, message, createDefaultYesAction(), createDefaultNoAction());
    }

    public ButtonType showImportantConfirmationYesNoAndWait(String title, String message) {
        return showMessageDialogAndWait(title, message, createDefaultYesAction(), createDefaultNoAction(), true);
    }

    private <T> void handleIgnoredFailure(AbstractWaitDialogTask<T> task) {
        boolean failureIgnored = task.isFailed() && task.getOnFailed() == null;
        if (failureIgnored) {
            throw ExceptionUtils.wrapNotRuntimeException(task.getException());
        }
    }

    public static MessageDialogAction createDefaultOKAction() {
        return createDefaultOKAction(null);
    }

    public static MessageDialogAction createDefaultOKAction(ExceptionalRunnable operation) {
        return MessageDialogAction.builder()
                .name("ok")
                .buttonType(ButtonType.OK)
                .operation(operation)
                .keyBinding(DEFAULT_OK_KEY_BINDING)
                .build();
    }

    public static MessageDialogAction createDefaultCancelAction() {
        return MessageDialogAction.builder()
                .name("cancel")
                .buttonType(ButtonType.CANCEL)
                .keyBinding(DEFAULT_CANCEL_KEY_BINDING)
                .build();
    }

    public static MessageDialogAction createDefaultYesAction() {
        return createDefaultYesAction(null);
    }

    public static MessageDialogAction createDefaultYesAction(ExceptionalRunnable operation) {
        return MessageDialogAction.builder()
                .name("yes")
                .buttonType(ButtonType.YES)
                .operation(operation)
                .keyBinding(DEFAULT_OK_KEY_BINDING)
                .build();
    }

    public static MessageDialogAction createDefaultNoAction() {
        return MessageDialogAction.builder()
                .name("no")
                .buttonType(ButtonType.NO)
                .keyBinding(DEFAULT_CANCEL_KEY_BINDING)
                .build();
    }

    public static MessageDialogAction createDefaultCloseAction() {
        return MessageDialogAction.builder()
                .name("close")
                .buttonType(ButtonType.CLOSE)
                .keyBinding(DEFAULT_OK_KEY_BINDING)
                .build();
    }

    public static MessageDialogAction createDefaultCloseAction(ExceptionalRunnable operation) {
        return MessageDialogAction.builder()
                .name("close")
                .buttonType(ButtonType.CLOSE)
                .operation(operation)
                .keyBinding(DEFAULT_OK_KEY_BINDING)
                .build();
    }
}
