package ru.kamuzta.rollfactorymgr.ui.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.*;
import ru.kamuzta.rollfactorymgr.model.client.ClientFilter;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.processor.ClientProcessor;
import ru.kamuzta.rollfactorymgr.ui.Screen;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ClientFindViewModel implements ViewModel, DisposableByEvent {

    private final ClientProcessor clientProcessor;
    private final EventBus eventBus;
    private final Screen screen;

    @Getter
    private ClientProperty clientProperty = ClientProperty.getSample();

    private ListProperty<ClientProperty> clientProperties = new SimpleListProperty<>();
    @Getter
    private ListProperty<ClientFilter> availableFilters = new SimpleListProperty<>(FXCollections.observableArrayList(ClientFilter.values()));
    @Getter
    private ListProperty<ClientFilter> selectedFilters = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Inject
    ClientFindViewModel(EventBus eventBus, ClientProcessor clientProcessor) {
        this.clientProcessor = clientProcessor;
        this.eventBus = eventBus;
        this.screen = Screen.CLIENT_FIND;
        eventBus.register(this);
    }


    public ObservableList<ClientProperty> getClientProperties() {
        return clientProperties.get();
    }

    public ListProperty<ClientProperty> clientPropertiesProperty() {
        return clientProperties;
    }

    public void setClientProperties(ObservableList<ClientProperty> clientProperties) {
        this.clientProperties.set(clientProperties);
    }

    @Subscribe
    public void onEvent(@NotNull UpdateClientTableEvent event) {
        log.info("Screen [" + screen + "] Action: " + "onEvent " + event);
        Platform.runLater(this::onFindClient);
    }

    void onFindClient() {
        log.info("Screen [" + screen + "] Action: " + "onFindClient");
        clientProcessor.updateRegistryFromServer();
        ObservableList<ClientFilter> clientFilters = selectedFilters.getValue();
        List<ClientProperty> clientPropertyList = clientProcessor.findClientByParams(
                clientFilters.contains(ClientFilter.ID) ? clientProperty.getId().getValue() : null,
                clientFilters.contains(ClientFilter.COMPANY_NAME) ? clientProperty.getCompanyName().getValue() : null,
                clientFilters.contains(ClientFilter.CREATION_DATE) ? clientProperty.getCreationDate().getValue() : null,
                clientFilters.contains(ClientFilter.CREATION_DATE) ? clientProperty.getCreationDate().getValue() : null,
                clientFilters.contains(ClientFilter.CITY) ? clientProperty.getCity().getValue() : null,
                clientFilters.contains(ClientFilter.ADDRESS) ? clientProperty.getAddress().getValue() : null,
                clientFilters.contains(ClientFilter.BUYER_NAME) ? clientProperty.getBuyerName().getValue() : null,
                clientFilters.contains(ClientFilter.PHONE) ? clientProperty.getPhone().getValue() : null,
                clientFilters.contains(ClientFilter.EMAIL) ? clientProperty.getEmail().getValue() : null
        ).stream().map(ClientProperty::new).collect(Collectors.toList());
        setClientProperties(FXCollections.observableArrayList(clientPropertyList));
    }

    void onEditClient(ClientProperty clientProperty) {
        log.info("Screen [" + screen + "] Action: " + "onEditClient");
        eventBus.post(new ShowEditClientEvent(clientProperty));
    }

    void onRemoveClient(Long id) {
        log.info("Screen [" + screen + "] Action: " + "onRemoveClient");
        clientProcessor.removeClientById(id);
        onFindClient();
    }

    @Subscribe
    @Override
    public void onDispose(@NotNull DisposeEvent event) {
        if (event.isSuitableFor(this)) {
            eventBus.unregister(this);
        }
    }
}