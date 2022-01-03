package ru.kamuzta.rollfactorymgr.event;

import org.jetbrains.annotations.NotNull;

/**
 * Dispose View by receiving Death Event
 */
public interface DisposableByEvent {

    void onDispose(@NotNull DisposeEvent event);
}
