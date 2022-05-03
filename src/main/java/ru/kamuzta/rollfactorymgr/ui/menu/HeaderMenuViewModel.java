package ru.kamuzta.rollfactorymgr.ui.menu;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.*;
import ru.kamuzta.rollfactorymgr.ui.Screen;
import ru.kamuzta.rollfactorymgr.ui.client.ClientRegistryView;
import ru.kamuzta.rollfactorymgr.ui.dialog.DialogHelper;
import ru.kamuzta.rollfactorymgr.ui.roll.RollFindView;
import ru.kamuzta.rollfactorymgr.ui.roll.RollRegistryView;

@Slf4j
public class HeaderMenuViewModel implements ViewModel, DisposableByEvent {

    private final EventBus eventBus;
    private final Screen screen;
    private final DialogHelper dialogHelper;

    @Inject
    HeaderMenuViewModel(EventBus eventBus, DialogHelper dialogHelper) {
        this.eventBus = eventBus;
        this.dialogHelper = dialogHelper;
        this.screen = Screen.HEADER_MENU;
        eventBus.register(this);
    }

    void onRollRegistry() {
        log.info("Screen ["+screen+"] Action: " + "onRollRegistry");
        eventBus.post(new ShowFullScreenEvent<>(Screen.ROLL_REGISTRY, RollRegistryView.class));
    }

    void onRollFind() {
        log.info("Screen ["+screen+"] Action: " + "onRollFind");
        eventBus.post(new ShowFullScreenEvent<>(Screen.ROLL_FIND, RollFindView.class));
    }

    void onRollCreate() {
        log.info("Screen ["+screen+"] Action: " + "onRollCreate");
        eventBus.post(new ShowCreateRollEvent());
    }

    void onClientRegistry() {
        log.info("Screen ["+screen+"] Action: " + "onClientRegistry");
        eventBus.post(new ShowFullScreenEvent<>(Screen.CLIENT_REGISTRY, ClientRegistryView.class));
    }

    void onClientFind() {
        log.info("Screen ["+screen+"] Action: " + "onClientFind");
    }

    void onClientCreate() {
        log.info("Screen ["+screen+"] Action: " + "onClientCreate");
        eventBus.post(new ShowCreateClientEvent());
    }

    void onOrderRegistry() {
        log.info("Screen ["+screen+"] Action: " + "onOrderRegistry");
    }

    void onOrderFind() {
        log.info("Screen ["+screen+"] Action: " + "onOrderFind");
    }

    void onOrderCreate() {
        log.info("Screen ["+screen+"] Action: " + "onOrderCreate");
    }

    void onConfigManager() {
        log.info("Screen ["+screen+"] Action: " + "onConfigManager");
    }

    void onConfigWorkplace() {
        log.info("Screen ["+screen+"] Action: " + "onConfigWorkplace");
    }

    void onConfigConnection() {
        log.info("Screen ["+screen+"] Action: " + "onConfigConnection");
    }

    void onAbout() {
        log.info("Screen ["+screen+"] Action: " + "onAbout");
    }

    void onExit() {
        log.info("Screen ["+screen+"] Action: " + "onExit");
        Platform.exit();
    }

    @Subscribe
    @Override
    public void onDispose(@NotNull DisposeEvent event) {
        if (event.isSuitableFor(this)) {
            eventBus.unregister(this);
        }
    }
}