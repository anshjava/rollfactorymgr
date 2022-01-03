package ru.kamuzta.rollfactorymgr.ui.roll;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.DisposableByEvent;
import ru.kamuzta.rollfactorymgr.event.DisposeEvent;
import ru.kamuzta.rollfactorymgr.model.*;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;
import ru.kamuzta.rollfactorymgr.ui.Screen;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RollRegistryViewModel implements ViewModel, DisposableByEvent {

    private final RollService rollService;
    private final EventBus eventBus;
    private final Screen screen;

    private ListProperty<RollProperty> rollProperties = new SimpleListProperty<>();

    @Inject
    RollRegistryViewModel(EventBus eventBus, RollService rollService) {
        this.rollService = rollService;
        this.eventBus = eventBus;
        this.screen = Screen.ROLL_REGISTRY;
        eventBus.register(this);
    }

    void onUpdateRollRegistry() {
        log.info("Screen [" + screen + "] Action: " + "onUpdateRollRegistry");
        rollService.updateRegistryFromServer();
        onRefreshTable();
    }

    void onEditRoll(String sku) {
        log.info("Screen [" + screen + "] Action: " + "onEditRoll");

    }

    void onRemoveRoll(String sku) {
        log.info("Screen [" + screen + "] Action: " + "onRemoveRoll");
        rollService.removeRollBySku(sku);
        onRefreshTable();
    }

    void onRefreshTable() {
        log.info("Screen [" + screen + "] Action: " + "onRefreshTable");
        List<RollProperty> rollPropertyList = rollService.getLocalRollRegistry().stream()
                .map(r -> new RollProperty(
                        new SimpleObjectProperty<>(r.getId()),
                        new SimpleStringProperty(r.getSku()),
                        new SimpleStringProperty(r.getRollType().getTypeName()),
                        new SimpleObjectProperty<>(r.getPaper().getWeight()),
                        new SimpleObjectProperty<>(r.getWidthType().getWidth()),
                        new SimpleObjectProperty<>(r.getCoreType().getDiameter()),
                        new SimpleObjectProperty<>(r.calculateLength()),
                        new SimpleObjectProperty<>(r.calculateDiameter()),
                        new SimpleObjectProperty<>(r.calculateWeight())
                )).collect(Collectors.toList());
        setRollProperties(FXCollections.observableArrayList(rollPropertyList));
    }

    @Subscribe
    @Override
    public void onDispose(@NotNull DisposeEvent event) {
        if (event.isSuitableFor(this)) {
            eventBus.unregister(this);
        }
    }

    public ObservableList<RollProperty> getRollProperties() {
        return rollProperties.get();
    }

    public ListProperty<RollProperty> rollPropertiesProperty() {
        return rollProperties;
    }

    public void setRollProperties(ObservableList<RollProperty> rollProperties) {
        this.rollProperties.set(rollProperties);
    }
}