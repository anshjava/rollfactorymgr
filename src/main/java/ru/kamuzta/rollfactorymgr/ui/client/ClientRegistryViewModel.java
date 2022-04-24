package ru.kamuzta.rollfactorymgr.ui.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.*;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.processor.ClientProcessor;
import ru.kamuzta.rollfactorymgr.ui.Screen;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ClientRegistryViewModel implements ViewModel, DisposableByEvent {

    private final ClientProcessor clientProcessor;
    private final EventBus eventBus;
    private final Screen screen;

    private ListProperty<ClientProperty> clientProperties = new SimpleListProperty<>();

    @Inject
    ClientRegistryViewModel(EventBus eventBus, ClientProcessor clientProcessor) {
        this.clientProcessor = clientProcessor;
        this.eventBus = eventBus;
        this.screen = Screen.CLIENT_REGISTRY;
        eventBus.register(this);
    }


    public ListProperty<ClientProperty> clientPropertiesProperty() {
        return clientProperties;
    }

    public void setClientProperties(ObservableList<ClientProperty> clientProperties) {
        this.clientProperties.set(clientProperties);
    }

    void onRefreshTable() {
        log.info("Screen [" + screen + "] Action: " + "onRefreshTable");
        List<ClientProperty> clientPropertyList = clientProcessor.getActiveClientsLocal().stream()
                .map(c -> new ClientProperty(
                        new SimpleObjectProperty<>(c.getId()),
                        new SimpleObjectProperty<>(c.getCreationDate()),
                        new SimpleStringProperty(c.getCompanyName()),
                        new SimpleStringProperty(c.getCity()),
                        new SimpleStringProperty(c.getAddress()),
                        new SimpleStringProperty(c.getBuyerName()),
                        new SimpleStringProperty(c.getPhone()),
                        new SimpleStringProperty(c.getEmail()),
                        new SimpleObjectProperty<>(c.getState())
                        )).collect(Collectors.toList());
        setClientProperties(FXCollections.observableArrayList(clientPropertyList));
    }

    @Subscribe
    public void onEvent(@NotNull UpdateClientTableEvent event) {
        log.info("Screen [" + screen + "] Action: " + "onEvent " + event);
        Platform.runLater(this::onUpdateClientRegistry);
    }

    void onUpdateClientRegistry() {
        log.info("Screen [" + screen + "] Action: " + "onUpdateClientRegistry");
        clientProcessor.updateRegistryFromServer();
        onRefreshTable();
    }

    void onEditClient(ClientProperty clientProperty) {
        log.info("Screen [" + screen + "] Action: " + "onEditClient");
        eventBus.post(new ShowEditClientEvent(clientProperty));
    }

    void onRemoveClient(Long id) {
        log.info("Screen [" + screen + "] Action: " + "onRemoveClient");
        clientProcessor.removeClientById(id);
        onRefreshTable();
    }

    @Subscribe
    @Override
    public void onDispose(@NotNull DisposeEvent event) {
        if (event.isSuitableFor(this)) {
            eventBus.unregister(this);
        }
    }
}