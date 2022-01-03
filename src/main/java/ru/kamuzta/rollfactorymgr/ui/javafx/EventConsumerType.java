package ru.kamuzta.rollfactorymgr.ui.javafx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

public enum EventConsumerType {
    FILTER {
        @Override
        public <T extends Event> void addToWindow(Window target, EventType<T> eventType, EventHandler<? super T> handler) {
            target.addEventFilter(eventType, handler);
        }

        @Override
        public <T extends Event> void addToPane(Pane target, EventType<T> eventType, EventHandler<? super T> handler) {
            target.addEventFilter(eventType, handler);
        }
    },
    HANDLER {
        @Override
        public <T extends Event> void addToWindow(Window target, EventType<T> eventType, EventHandler<? super T> handler) {
            target.addEventHandler(eventType, handler);
        }

        @Override
        public <T extends Event> void addToPane(Pane target, EventType<T> eventType, EventHandler<? super T> handler) {
            target.addEventHandler(eventType, handler);
        }
    };

    public abstract <T extends Event> void addToWindow(Window target, EventType<T> eventType, EventHandler<? super T> handler);

    public abstract <T extends Event> void addToPane(Pane target, EventType<T> eventType, EventHandler<? super T> handler);
}
