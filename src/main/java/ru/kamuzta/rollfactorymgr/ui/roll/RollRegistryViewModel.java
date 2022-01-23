package ru.kamuzta.rollfactorymgr.ui.roll;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.*;
import ru.kamuzta.rollfactorymgr.model.*;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;
import ru.kamuzta.rollfactorymgr.ui.Screen;
import ru.kamuzta.rollfactorymgr.ui.dialog.DialogHelper;
import ru.kamuzta.rollfactorymgr.ui.menu.MenuView;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RollRegistryViewModel implements ViewModel, DisposableByEvent {

    private final RollService rollService;
    private final EventBus eventBus;
    private final DialogHelper dialogHelper;
    private final Screen screen;

    private ListProperty<RollProperty> rollProperties = new SimpleListProperty<>();

    @Inject
    RollRegistryViewModel(EventBus eventBus, RollService rollService, DialogHelper dialogHelper) {
        this.rollService = rollService;
        this.eventBus = eventBus;
        this.dialogHelper = dialogHelper;
        this.screen = Screen.ROLL_REGISTRY;
        eventBus.register(this);
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
    public void onEvent(@NotNull UpdateRollRegistryEvent event) {
        log.info("Screen [" + screen + "] Action: " + "onEvent " + event);
        Platform.runLater(this::onUpdateRollRegistry);
    }

    void onUpdateRollRegistry() {
        log.info("Screen [" + screen + "] Action: " + "onUpdateRollRegistry");
        rollService.updateRegistryFromServer();
        onRefreshTable();
    }

    void onEditRoll(RollProperty rollProperty) {
        log.info("Screen [" + screen + "] Action: " + "onEditRoll");
        eventBus.post(new ShowEditRollEvent(rollProperty));
    }

    void onRemoveRoll(String sku) {
        log.info("Screen [" + screen + "] Action: " + "onRemoveRoll");
        rollService.removeRollBySku(sku);
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