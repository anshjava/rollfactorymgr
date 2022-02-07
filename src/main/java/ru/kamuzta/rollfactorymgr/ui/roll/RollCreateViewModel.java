package ru.kamuzta.rollfactorymgr.ui.roll;

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
import ru.kamuzta.rollfactorymgr.event.DisposableByEvent;
import ru.kamuzta.rollfactorymgr.event.DisposeEvent;
import ru.kamuzta.rollfactorymgr.event.ScreenEvent;
import ru.kamuzta.rollfactorymgr.event.UpdateRollRegistryEvent;
import ru.kamuzta.rollfactorymgr.model.*;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;
import ru.kamuzta.rollfactorymgr.ui.Screen;

import java.math.BigDecimal;

@Slf4j
public class RollCreateViewModel implements ViewModel, DisposableByEvent {

    private final RollService rollService;
    private final EventBus eventBus;
    private final Screen screen;
    @Getter
    private RollProperty rollProperty = RollProperty.getSample();

    @Inject
    RollCreateViewModel(EventBus eventBus, RollService rollService) {
        this.rollService = rollService;
        this.eventBus = eventBus;
        this.screen = Screen.ROLL_CREATE;
        eventBus.register(this);
    }

    void createRoll() {
        log.info("Screen [" + screen + "] Action: " + "createRoll");
        rollService.createRoll(rollProperty.getSku().getValue(),
                rollProperty.getRollType().getValue(),
                rollProperty.getPaper().getValue(),
                rollProperty.getWidthType().getValue(),
                rollProperty.getCoreType().getValue(),
                rollProperty.getMainValue().getValue());
        Platform.runLater(() -> eventBus.post(new UpdateRollRegistryEvent()));
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

    public BigDecimal calculateWeight() {
        return rollProperty.calculateWeight().getValue();
    }

    public BigDecimal calculateLength() {
        return rollProperty.calculateLength().getValue();
    }

    public BigDecimal calculateDiameter() {
        return rollProperty.calculateDiameter().getValue();
    }
}