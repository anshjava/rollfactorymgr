package ru.kamuzta.rollfactorymgr.ui.dialog;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import ru.kamuzta.rollfactorymgr.event.WaitDialogOpenEvent;
import ru.kamuzta.rollfactorymgr.notifier.NotificationEvent;
import ru.kamuzta.rollfactorymgr.notifier.NotificationListener;
import ru.kamuzta.rollfactorymgr.notifier.Notifier;
import ru.kamuzta.rollfactorymgr.notifier.NotifierButton;
import ru.kamuzta.rollfactorymgr.service.interrupt.InterruptingService;
import ru.kamuzta.rollfactorymgr.ui.DialogManaged;
import ru.kamuzta.rollfactorymgr.ui.PeriodicalInvoker;
import ru.kamuzta.rollfactorymgr.ui.ValueChangeListener;

import java.util.Optional;

public class WaitDialogViewModel implements ViewModel, NotificationListener, DialogManaged {

    private final EventBus eventBus;
    private final InterruptingService interruptingService;

    private Optional<Notifier> notifiableHolder = Optional.empty();
    private Optional<PeriodicalInvoker> notifiableCountdownInvokerHolder = Optional.empty();

    private StringProperty message = new SimpleStringProperty();
    private ObjectProperty<Parent> interactParent = new SimpleObjectProperty<>();
    private IntegerProperty countdown = new SimpleIntegerProperty();
    private ObjectProperty<ButtonType> result = new SimpleObjectProperty<>();

    private ObjectProperty<Dialog> rootDialog = new SimpleObjectProperty<>();
    private ListProperty<NotifierButton> buttons = new SimpleListProperty<>();

    private BooleanProperty yesButtonDisable = new SimpleBooleanProperty();
    private BooleanProperty noButtonDisable = new SimpleBooleanProperty();
    private BooleanProperty cancelButtonDisable = new SimpleBooleanProperty();

    /**
     * Window close event. It is necessary to send a signal to the author of Event-a that the window has been closed.
     */
    private Runnable onCloseNotifyEvent;

    private final ValueChangeListener<Boolean> canInterruptListener = newValue -> cancelButtonDisable.set(!newValue);

    @Inject
    WaitDialogViewModel(EventBus eventBus, InterruptingService interruptingService) {
        this.eventBus = eventBus;
        this.interruptingService = interruptingService;
    }

    public void onLongtimeOperation(WaitDialogOpenEvent dialogOpenEvent) {
        AbstractWaitDialogTask task = dialogOpenEvent.getTask();

        task.setOnDone(result -> close());

        setMessage(dialogOpenEvent.getMessage());

        notifiableHolder = dialogOpenEvent.getNotifiableHolder();

        if (notifiableHolder.isPresent()) {
            notifiableHolder.get().addNotificationListener(this);
        }

        if (task.isDone()) {
            close();
        }
    }

    @Override
    public void notified(NotificationEvent event) {

        notifiableCountdownInvokerHolder.ifPresent(PeriodicalInvoker::stop);

        notifiableCountdownInvokerHolder = Optional.of(new NotifiableCountdownInvoker(event.getInterruptPeriod()));

        initMessages(event);

        notifiableCountdownInvokerHolder.get().start(event);
    }

    private Parent getInteractParent(NotificationEvent event) {
        return null;
    }

    private void initMessages(NotificationEvent event) {
        Platform.runLater(() -> {
            setMessage(event.getMessage());
            setButtons(FXCollections.observableArrayList(event.getButtons()));
            setInteractParent(getInteractParent(event));
            setOnCloseNotifyEvent(event.getOnTimeout());
            if (event.isInterruptible()) {
                interruptingService.addHasActiveInterruptListener(canInterruptListener);
            } else {
                interruptingService.removeListener(canInterruptListener);
            }
        });
    }

    private void close() {
        notifiableHolder.ifPresent(notifiable -> notifiable.removeNotificationListener(this));

        notifiableCountdownInvokerHolder.ifPresent(PeriodicalInvoker::stop);
        if (onCloseNotifyEvent !=null){
            onCloseNotifyEvent.run();
        }
        this.interruptingService.removeListener(canInterruptListener);
        setResult(ButtonType.CLOSE);
    }

    @Override
    public void setDialog(Dialog dialog){
        rootDialog.setValue(dialog);
    };

    public Dialog getRootDialog() {
        return rootDialog.get();
    }

    private class NotifiableCountdownInvoker extends PeriodicalInvoker {

        public static final int DEFAULT_PERIOD_SEC = 1;

        public NotifiableCountdownInvoker(int enableTimeSec) {
            super(DEFAULT_PERIOD_SEC, enableTimeSec);
        }

        @Override
        protected void update(NotificationEvent event, int count) {
            setCountdown(count);
        }

        @Override
        protected void enable(NotificationEvent event) {
        }

        @Override
        protected void disable(NotificationEvent event) {
            if (event.getOnTimeout() != null) {
                event.getOnTimeout().run();
            }
        }
    }

    // ===============================================================================================================

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public int getCountdown() {
        return countdown.get();
    }

    public IntegerProperty countdownProperty() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown.set(countdown);
    }

    public ButtonType getResult() {
        return result.get();
    }

    public ObjectProperty<ButtonType> resultProperty() {
        return result;
    }

    public void setResult(ButtonType result) {
        this.result.set(result);
    }


    public ObjectProperty<Parent> interactParentProperty() {
        return interactParent;
    }

    public void setInteractParent(Parent interactParent) {
        this.interactParent.set(interactParent);
    }

    public ObservableList<NotifierButton> getButtons() {
        return buttons.get();
    }

    public ListProperty<NotifierButton> buttonsProperty() {
        return buttons;
    }

    public void setButtons(ObservableList<NotifierButton> buttons) {
        this.buttons.set(buttons);
    }


    public void setOnCloseNotifyEvent(Runnable onClose) {
        this.onCloseNotifyEvent = onClose;
    }

    public BooleanProperty yesButtonDisableProperty() {
        return yesButtonDisable;
    }


    public BooleanProperty noButtonDisableProperty() {
        return noButtonDisable;
    }

    public BooleanProperty cancelButtonDisableProperty() {
        return cancelButtonDisable;
    }
}
