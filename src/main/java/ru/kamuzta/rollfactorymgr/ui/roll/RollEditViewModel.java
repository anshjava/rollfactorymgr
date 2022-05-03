package ru.kamuzta.rollfactorymgr.ui.roll;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.DisposableByEvent;
import ru.kamuzta.rollfactorymgr.event.DisposeEvent;
import ru.kamuzta.rollfactorymgr.event.ScreenEvent;
import ru.kamuzta.rollfactorymgr.event.UpdateRollTableEvent;
import ru.kamuzta.rollfactorymgr.model.roll.Roll;
import ru.kamuzta.rollfactorymgr.model.roll.RollProperty;
import ru.kamuzta.rollfactorymgr.processor.RollProcessor;
import ru.kamuzta.rollfactorymgr.ui.Screen;

import java.math.BigDecimal;

@Slf4j
public class RollEditViewModel implements ViewModel, DisposableByEvent {

    private final RollProcessor rollProcessor;
    private final EventBus eventBus;
    private final Screen screen;

    @Getter
    private ObjectProperty<RollProperty> rollProperty = new SimpleObjectProperty<>();

    @Inject
    RollEditViewModel(EventBus eventBus, RollProcessor rollProcessor) {
        this.rollProcessor = rollProcessor;
        this.eventBus = eventBus;
        this.screen = Screen.ROLL_EDIT;
        eventBus.register(this);
    }

    void editRoll() {
        log.info("Screen [" + screen + "] Action: " + "editRoll");
        rollProcessor.updateRoll(Roll.builder().id(rollProperty.getValue().getId().getValue())
                .sku(rollProperty.getValue().getSku().getValue())
                .rollType(rollProperty.getValue().getRollType().getValue())
                .paper(rollProperty.getValue().getPaper().getValue())
                .widthType(rollProperty.getValue().getWidthType().getValue())
                .coreType(rollProperty.getValue().getCoreType().getValue())
                .mainValue(rollProperty.getValue().getMainValue().getValue())
                .state(rollProperty.getValue().getState().getValue())
                .build());
        Platform.runLater(() -> eventBus.post(new UpdateRollTableEvent()));
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

    public void setRollProperty(RollProperty rollProperty) {
        this.rollProperty.set(rollProperty);
    }

    public BigDecimal calculateWeight() {
        return rollProperty.getValue().calculateWeight().getValue();
    }

    public BigDecimal calculateLength() {
        return rollProperty.getValue().calculateLength().getValue();
    }

    public BigDecimal calculateDiameter() {
        return rollProperty.getValue().calculateDiameter().getValue();
    }
}