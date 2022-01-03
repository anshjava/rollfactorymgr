package ru.kamuzta.rollfactorymgr.ui.menu;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.DisposableByEvent;
import ru.kamuzta.rollfactorymgr.event.DisposeEvent;
import ru.kamuzta.rollfactorymgr.event.RollRegistryOpenEvent;
import ru.kamuzta.rollfactorymgr.ui.Screen;

@Slf4j
public class HeaderMenuViewModel implements ViewModel, DisposableByEvent {

    private final EventBus eventBus;
    private final Screen screen;

    @Inject
    HeaderMenuViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.screen = Screen.HEADER_MENU;
        eventBus.register(this);
    }

    void onRollRegistry() {
        log.info("Screen ["+screen+"] Action: " + "onRollRegistry");
        eventBus.post(new RollRegistryOpenEvent());
    }

    void onRollFind() {
        log.info("Screen ["+screen+"] Action: " + "onRollFind");
    }

    void onRollCreate() {
        log.info("Screen ["+screen+"] Action: " + "onRollCreate");
    }

    void onClientRegistry() {
        log.info("Screen ["+screen+"] Action: " + "onClientRegistry");
    }

    void onClientFind() {
        log.info("Screen ["+screen+"] Action: " + "onClientFind");
    }

    void onClientCreate() {
        log.info("Screen ["+screen+"] Action: " + "onClientCreate");
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