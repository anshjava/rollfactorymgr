package ru.kamuzta.rollfactorymgr.model.order;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.model.client.Client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order wrapper for FX
 */
@Getter
@AllArgsConstructor
public class OrderProperty {
    private final ObjectProperty<Long> id;
    private final ObjectProperty<LocalDateTime> creationDateTime;
    private final ObjectProperty<Client> client;
    private final ObjectProperty<OrderState> state;
    private final ObjectProperty<List<OrderLine>> lines;

    //copy constructor
    public OrderProperty(OrderProperty that) {
        this.id = new SimpleObjectProperty<>(that.id.getValue());
        this.creationDateTime = new SimpleObjectProperty<>(that.creationDateTime.getValue());
        this.client = new SimpleObjectProperty<>(that.client.getValue());
        this.state = new SimpleObjectProperty<>(that.state.getValue());
        this.lines = new SimpleObjectProperty<>(that.lines.getValue());
    }

    public ObjectProperty<BigDecimal> calculateWeight() {
        return new SimpleObjectProperty<>(lines.getValue().stream().map(OrderLine::calculateWeight).reduce(BigDecimal.ZERO,BigDecimal::add));
    }

}
