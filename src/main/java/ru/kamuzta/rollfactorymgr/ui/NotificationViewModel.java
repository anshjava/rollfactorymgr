package ru.kamuzta.rollfactorymgr.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationViewModel implements ViewModel {

    private StringProperty text = new SimpleStringProperty();
    private BooleanProperty close = new SimpleBooleanProperty(false);

    private final EventBus eventBus;

    @Inject
    public NotificationViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public BooleanProperty closeProperty() {
        return close;
    }
}
