package ru.kamuzta.rollfactorymgr.ui.roll;

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
import ru.kamuzta.rollfactorymgr.event.DisposableByEvent;
import ru.kamuzta.rollfactorymgr.event.DisposeEvent;
import ru.kamuzta.rollfactorymgr.event.ShowEditRollEvent;
import ru.kamuzta.rollfactorymgr.event.UpdateRollTableEvent;
import ru.kamuzta.rollfactorymgr.model.roll.RollFilter;
import ru.kamuzta.rollfactorymgr.model.roll.RollProperty;
import ru.kamuzta.rollfactorymgr.processor.RollProcessor;
import ru.kamuzta.rollfactorymgr.ui.Screen;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class RollFindViewModel implements ViewModel, DisposableByEvent {

    private final RollProcessor rollProcessor;
    private final EventBus eventBus;
    private final Screen screen;

    @Getter
    private RollProperty rollProperty = RollProperty.getSample();

    private ListProperty<RollProperty> rollProperties = new SimpleListProperty<>();
    @Getter
    private ListProperty<RollFilter> availableFilters = new SimpleListProperty<>(FXCollections.observableArrayList(RollFilter.values()));
    @Getter
    private ListProperty<RollFilter> selectedFilters = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Inject
    RollFindViewModel(EventBus eventBus, RollProcessor rollProcessor) {
        this.rollProcessor = rollProcessor;
        this.eventBus = eventBus;
        this.screen = Screen.ROLL_FIND;
        eventBus.register(this);
    }


    public ListProperty<RollProperty> rollPropertiesProperty() {
        return rollProperties;
    }

    public void setRollProperties(ObservableList<RollProperty> rollProperties) {
        this.rollProperties.set(rollProperties);
    }

    @Subscribe
    public void onEvent(@NotNull UpdateRollTableEvent event) {
        log.info("Screen [" + screen + "] Action: " + "onEvent " + event);
        Platform.runLater(this::onFindRoll);
    }

    void onFindRoll() {
        log.info("Screen [" + screen + "] Action: " + "onFindRoll");
        rollProcessor.updateRegistryFromServer();
        ObservableList<RollFilter> rollFilters = selectedFilters.getValue();
        List<RollProperty> rollPropertyList = rollProcessor.findRollByParams(
                rollFilters.contains(RollFilter.ID) ? rollProperty.getId().getValue() : null,
                rollFilters.contains(RollFilter.SKU) ? rollProperty.getSku().getValue() : null,
                rollFilters.contains(RollFilter.ROLL_TYPE) ? rollProperty.getRollType().getValue() : null,
                rollFilters.contains(RollFilter.PAPER) ? rollProperty.getPaper().getValue() : null,
                rollFilters.contains(RollFilter.WIDTH_TYPE) ? rollProperty.getWidthType().getValue() : null,
                rollFilters.contains(RollFilter.CORE_TYPE) ? rollProperty.getCoreType().getValue() : null,
                rollFilters.contains(RollFilter.MAIN_VALUE) ? rollProperty.getMainValue().getValue() : null
        ).stream().map(r -> new RollProperty(
                new SimpleObjectProperty<>(r.getId()),
                new SimpleStringProperty(r.getSku()),
                new SimpleObjectProperty<>(r.getRollType()),
                new SimpleObjectProperty<>(r.getPaper()),
                new SimpleObjectProperty<>(r.getWidthType()),
                new SimpleObjectProperty<>(r.getCoreType()),
                new SimpleObjectProperty<>(r.getMainValue()),
                new SimpleObjectProperty<>(r.getState())
        )).collect(Collectors.toList());
        setRollProperties(FXCollections.observableArrayList(rollPropertyList));
    }

    void onEditRoll(RollProperty rollProperty) {
        log.info("Screen [" + screen + "] Action: " + "onEditRoll");
        eventBus.post(new ShowEditRollEvent(rollProperty));
        //TODO update table afterEdit
    }

    void onRemoveRoll(String sku) {
        log.info("Screen [" + screen + "] Action: " + "onRemoveRoll");
        rollProcessor.removeRollBySku(sku);
        onFindRoll();
    }

    @Subscribe
    @Override
    public void onDispose(@NotNull DisposeEvent event) {
        if (event.isSuitableFor(this)) {
            eventBus.unregister(this);
        }
    }
}