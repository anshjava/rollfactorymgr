package ru.kamuzta.rollfactorymgr.ui.order;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.*;
import ru.kamuzta.rollfactorymgr.model.order.OrderProperty;
import ru.kamuzta.rollfactorymgr.processor.OrderProcessor;
import ru.kamuzta.rollfactorymgr.ui.Screen;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OrderRegistryViewModel implements ViewModel, DisposableByEvent {

    private final OrderProcessor orderProcessor;
    private final EventBus eventBus;
    private final Screen screen;

    private ListProperty<OrderProperty> orderProperties = new SimpleListProperty<>();

    @Inject
    OrderRegistryViewModel(EventBus eventBus, OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
        this.eventBus = eventBus;
        this.screen = Screen.ORDER_REGISTRY;
        eventBus.register(this);
    }


    public ListProperty<OrderProperty> rollPropertiesProperty() {
        return orderProperties;
    }

    public void setRollProperties(ObservableList<OrderProperty> orderProperties) {
        this.orderProperties.set(orderProperties);
    }

    void onRefreshTable() {
        log.info("Screen [" + screen + "] Action: " + "onRefreshTable");
        List<OrderProperty> orderPropertyList = orderProcessor.getLocalRegistry().stream()
                .map(order -> new OrderProperty(
                        new SimpleObjectProperty<>(order.getId()),
                        new SimpleObjectProperty<>(order.getCreationDateTime()),
                        new SimpleObjectProperty<>(order.getClient()),
                        new SimpleObjectProperty<>(order.getState()),
                        new SimpleObjectProperty<>(order.getLines())
                )).collect(Collectors.toList());
        setRollProperties(FXCollections.observableArrayList(orderPropertyList));
    }

    @Subscribe
    public void onEvent(@NotNull UpdateRollTableEvent event) {
        log.info("Screen [" + screen + "] Action: " + "onEvent " + event);
        Platform.runLater(this::onUpdateOrderRegistry);
    }

    void onUpdateOrderRegistry() {
        log.info("Screen [" + screen + "] Action: " + "onUpdateOrderRegistry");
        orderProcessor.updateRegistryFromServer();
        onRefreshTable();
    }

    void onEditOrder(OrderProperty orderProperty) {
        log.info("Screen [" + screen + "] Action: " + "onEditOrder");
        eventBus.post(new ShowEditOrderEvent(orderProperty));
    }

    void onCancelOrder(Long id) {
        log.info("Screen [" + screen + "] Action: " + "onCancelOrder");
        orderProcessor.cancelOrderById(id);
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