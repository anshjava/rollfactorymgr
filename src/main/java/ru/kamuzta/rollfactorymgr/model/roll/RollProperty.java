package ru.kamuzta.rollfactorymgr.model.roll;

import javafx.beans.property.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.utils.RollCalculator;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Roll wrapper for FX
 */
@Getter
@AllArgsConstructor
public class RollProperty {
    private final ObjectProperty<Long> id;
    private final StringProperty sku;
    private final ObjectProperty<RollType> rollType;
    private final ObjectProperty<Paper> paper;
    private final ObjectProperty<WidthType> widthType;
    private final ObjectProperty<CoreType> coreType;
    private final ObjectProperty<BigDecimal> mainValue;
    private final ObjectProperty<RollState> state;

    //copy constructor
    public RollProperty(RollProperty that) {
        this.id = new SimpleObjectProperty<>(that.id.getValue());
        this.sku = new SimpleStringProperty(that.sku.getValue());
        this.rollType = new SimpleObjectProperty<>(that.rollType.getValue());
        this.paper = new SimpleObjectProperty<>(that.paper.getValue());
        this.widthType = new SimpleObjectProperty<>(that.widthType.getValue());
        this.coreType = new SimpleObjectProperty<>(that.coreType.getValue());
        this.mainValue = new SimpleObjectProperty<>(that.mainValue.getValue());
        this.state = new SimpleObjectProperty<>(that.state.getValue());
    }

    public static RollProperty getSample() {
        return new RollProperty(new SimpleObjectProperty<>(null),
                new SimpleStringProperty(""),
                new SimpleObjectProperty<>(RollType.LENGTH),
                new SimpleObjectProperty<>(Paper.NTC44),
                new SimpleObjectProperty<>(WidthType.WIDTH_57),
                new SimpleObjectProperty<>(CoreType.CORE_12),
                new SimpleObjectProperty<>(null),
                new SimpleObjectProperty<>(RollState.ACTIVE)
        );
    }

    public ObjectProperty<BigDecimal> calculateLength() {
        return new SimpleObjectProperty<>(RollCalculator.calculateLength(rollType.getValue(), paper.getValue(), coreType.getValue(), mainValue.getValue()));
    }

    public ObjectProperty<BigDecimal> calculateDiameter() {
        return new SimpleObjectProperty<>(RollCalculator.calculateDiameter(rollType.getValue(), paper.getValue(), coreType.getValue(), mainValue.getValue()));
    }

    public ObjectProperty<BigDecimal> calculateWeight() {
        return new SimpleObjectProperty<>(RollCalculator.calculateWeight(rollType.getValue(), paper.getValue(), widthType.getValue(), coreType.getValue(), mainValue.getValue()));
    }

}
