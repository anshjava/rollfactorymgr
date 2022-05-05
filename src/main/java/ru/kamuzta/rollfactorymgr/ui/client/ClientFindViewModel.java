package ru.kamuzta.rollfactorymgr.ui.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.*;
import ru.kamuzta.rollfactorymgr.model.client.ClientFilter;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.processor.ClientProcessor;
import ru.kamuzta.rollfactorymgr.ui.Screen;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ClientFindViewModel implements ViewModel, DisposableByEvent {

    private final ClientProcessor clientProcessor;
    private final EventBus eventBus;
    private final Screen screen;

    //search filters
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>(null);
    private final ObjectProperty<LocalDate> creationDateFrom = new SimpleObjectProperty<>(LocalDate.now());
    private final ObjectProperty<LocalDate> creationDateTo = new SimpleObjectProperty<>(LocalDate.now());
    private final StringProperty companyName = new SimpleStringProperty("");
    private final StringProperty city = new SimpleStringProperty("");
    private final StringProperty address = new SimpleStringProperty("");
    private final StringProperty buyerName = new SimpleStringProperty("");
    private final StringProperty phone = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");

    private ListProperty<ClientProperty> clientProperties = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<ClientFilter> availableFilters = new SimpleListProperty<>(FXCollections.observableArrayList(ClientFilter.values()));
    private ListProperty<ClientFilter> selectedFilters = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Inject
    ClientFindViewModel(EventBus eventBus, ClientProcessor clientProcessor) {
        this.clientProcessor = clientProcessor;
        this.eventBus = eventBus;
        this.screen = Screen.CLIENT_FIND;
        eventBus.register(this);
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
                clientFilters.contains(ClientFilter.ID) ? id.getValue() : null,
                clientFilters.contains(ClientFilter.COMPANY_NAME) ? companyName.getValue() : null,
                clientFilters.contains(ClientFilter.CREATION_DATE) ? creationDateFrom.getValue() : null,
                clientFilters.contains(ClientFilter.CREATION_DATE) ? creationDateTo.getValue() : null,
                clientFilters.contains(ClientFilter.CITY) ? city.getValue() : null,
                clientFilters.contains(ClientFilter.ADDRESS) ? address.getValue() : null,
                clientFilters.contains(ClientFilter.BUYER_NAME) ? buyerName.getValue() : null,
                clientFilters.contains(ClientFilter.PHONE) ? phone.getValue() : null,
                clientFilters.contains(ClientFilter.EMAIL) ? email.getValue() : null
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