package ru.kamuzta.rollfactorymgr.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import ru.kamuzta.rollfactorymgr.event.KeyPressedEvent;

@Singleton
public class KeyEventFilter implements EventHandler<KeyEvent> {
    private final EventBus eventBus;

    @Inject
    KeyEventFilter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void handle(KeyEvent event) {
        eventBus.post(new KeyPressedEvent(event));
    }
}
