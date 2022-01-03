package ru.kamuzta.rollfactorymgr.event;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;

public class ScreenEvent extends Event {

    private boolean requireFocusOnParent = true;
    private ButtonType result = null;

    private static final long serialVersionUID = 1L;

    public static final EventType<ScreenEvent> ANY = new EventType<>("ANY");

    public static final EventType<ScreenEvent> SCREEN_CLOSE_REQUEST = new EventType<>("SCREEN_CLOSE_REQUEST");

    public ScreenEvent(@NamedArg("target") Parent target, @NamedArg("eventType") EventType<? extends Event> eventType) {
        super(target, target, eventType);
    }

    public ScreenEvent(final @NamedArg("eventType") EventType<? extends Event> eventType) {
        super(eventType);
    }

    public ScreenEvent withoutFocusOnParent() {
        this.requireFocusOnParent = false;
        return this;
    }

    public ScreenEvent withFocusOnParent() {
        this.requireFocusOnParent = true;
        return this;
    }

    public ScreenEvent withResult(ButtonType result) {
        this.result = result;
        return this;
    }

    public boolean isRequireFocusOnParent() {
        return requireFocusOnParent;
    }

    public ButtonType getResult() {
        return result;
    }
}
