package ru.kamuzta.rollfactorymgr.ui.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
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
public class ClientCreateViewModel implements ViewModel, DisposableByEvent {

    private final ClientProcessor clientProcessor;
    private final EventBus eventBus;
    private final Screen screen;
    @Getter
    private ClientProperty clientProperty = ClientProperty.getSample();

    @Inject
    ClientCreateViewModel(EventBus eventBus, ClientProcessor clientProcessor) {
        this.clientProcessor = clientProcessor;
        this.eventBus = eventBus;
        this.screen = Screen.CLIENT_CREATE;
        eventBus.register(this);
    }

    void createClient() {
        log.info("Screen [" + screen + "] Action: " + "createClient");
        clientProcessor.createClient(clientProperty.getCreationDate().getValue(),
                clientProperty.getCompanyName().getValue(),
                clientProperty.getCity().getValue(),
                clientProperty.getAddress().getValue(),
                clientProperty.getBuyerName().getValue(),
                clientProperty.getPhone().getValue(),
                clientProperty.getEmail().getValue());
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
}