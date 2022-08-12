package ru.kamuzta.rollfactorymgr.model.order;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.model.roll.Roll;
import ru.kamuzta.rollfactorymgr.utils.RollCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Order wrapper for FX
 */
@Getter
@AllArgsConstructor
public class OrderLineProperty {
    private final ObjectProperty<Long> id;
    private final ObjectProperty<Roll> roll;
    private final ObjectProperty<Integer> quantity;
    private final ObjectProperty<OrderState> state;

    //copy constructor
    public OrderLineProperty(OrderLineProperty that) {
        this.id = new SimpleObjectProperty<>(that.id.getValue());
        this.roll = new SimpleObjectProperty<>(that.roll.getValue());
        this.quantity = new SimpleObjectProperty<>(that.quantity.getValue());
        this.state = new SimpleObjectProperty<>(that.state.getValue());
    }

    public ObjectProperty<BigDecimal> calculateWeight() {
        return new SimpleObjectProperty<>(RollCalculator.calculateWeight(roll.getValue())
                .multiply(BigDecimal.valueOf(quantity.getValue())).setScale(2, RoundingMode.HALF_UP));
    }

}
