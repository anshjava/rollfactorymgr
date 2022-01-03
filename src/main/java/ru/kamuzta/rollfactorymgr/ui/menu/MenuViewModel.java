package ru.kamuzta.rollfactorymgr.ui.menu;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.DisposableByEvent;
import ru.kamuzta.rollfactorymgr.event.DisposeEvent;
import ru.kamuzta.rollfactorymgr.ui.Screen;

@Slf4j
public class MenuViewModel implements ViewModel, DisposableByEvent {

    private final EventBus eventBus;
    private final Screen screen;

    @Inject
    MenuViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.screen = Screen.MENU;
        eventBus.register(this);
    }

    @Subscribe
    @Override
    public void onDispose(@NotNull DisposeEvent event) {
        if (event.isSuitableFor(this)) {
            eventBus.unregister(this);
            eventBus.post(new DisposeEvent<>(HeaderMenuViewModel.class));
        }
    }
}