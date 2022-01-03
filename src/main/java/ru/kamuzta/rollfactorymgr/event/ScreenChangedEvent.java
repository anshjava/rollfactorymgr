package ru.kamuzta.rollfactorymgr.event;

import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.ui.Screen;

public class ScreenChangedEvent extends UIEvent {
    private final Screen screen;

    public ScreenChangedEvent(@NotNull Screen screen) {
        this.screen = screen;
    }

    @NotNull
    public Screen getScreen() {
        return screen;
    }
}
