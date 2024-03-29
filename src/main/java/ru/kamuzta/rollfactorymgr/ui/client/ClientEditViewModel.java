package ru.kamuzta.rollfactorymgr.ui.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.*;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.processor.ClientProcessor;
import ru.kamuzta.rollfactorymgr.ui.Screen;

@Slf4j
public class ClientEditViewModel implements ViewModel, DisposableByEvent {

    private final ClientProcessor clientProcessor;
    private final EventBus eventBus;
    private final Screen screen;

    @Getter
    private ObjectProperty<ClientProperty> clientProperty = new SimpleObjectProperty<>();

    @Inject
    ClientEditViewModel(EventBus eventBus, ClientProcessor clientProcessor) {
        this.clientProcessor = clientProcessor;
        this.eventBus = eventBus;
        this.screen = Screen.CLIENT_EDIT;
        eventBus.register(this);
    }

    void editClient() {
        log.info("Screen [" + screen + "] Action: " + "editClient");
        clientProcessor.updateClient(clientProperty.get().toClient());
        Platform.runLater(() -> eventBus.post(new UpdateClientTableEvent()));
    }

    void close(ActionEvent actionEvent) {
        log.info("Screen [" + screen + "] Action: " + "close");
        Event.fireEvent(actionEvent.getTarget(), new ScreenEvent(ScreenEvent.SCREEN_CLOSE_REQUEST));
    }

    @Subscribe
    @Override
    public void onDispose(@NotNull DisposeEvent event) {
        if (event.isSuitableFor(this)) {
            eventBus.unregister(this);
        }
    }

    public void setClientProperty(ClientProperty clientProperty) {
        this.clientProperty.set(clientProperty);
    }
}