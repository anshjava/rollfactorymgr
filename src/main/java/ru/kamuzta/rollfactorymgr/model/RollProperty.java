package ru.kamuzta.rollfactorymgr.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

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

    public static RollProperty getEmpty() {
        return new RollProperty(new SimpleObjectProperty<>(),
                new SimpleStringProperty(),
                new SimpleObjectProperty<>(),
                new SimpleObjectProperty<>(),
                new SimpleObjectProperty<>(),
                new SimpleObjectProperty<>(),
                new SimpleObjectProperty<>()
                );
    }

    public static RollProperty getSample() {
        return new RollProperty(new SimpleObjectProperty<>(0L),
                new SimpleStringProperty(""),
                new SimpleObjectProperty<>(RollType.LENGTH),
                new SimpleObjectProperty<>(Paper.NTC44),
                new SimpleObjectProperty<>(WidthType.WIDTH_57),
                new SimpleObjectProperty<>(CoreType.CORE_12),
                new SimpleObjectProperty<>(BigDecimal.ZERO)
        );
    }

    private Roll constructRoll() {
        return new Roll(id.getValue(),
                sku.getValue(),
                rollType.getValue(),
                paper.getValue(),
                widthType.getValue(),
                coreType.getValue(),
                mainValue.getValue());
    }

    public ObjectProperty<BigDecimal> calculateLength() {
        return new SimpleObjectProperty<>(constructRoll().calculateLength());
    }

    public ObjectProperty<BigDecimal> calculateDiameter() {
        return new SimpleObjectProperty<>(constructRoll().calculateDiameter());
    }

    public ObjectProperty<BigDecimal> calculateWeight() {
        return new SimpleObjectProperty<>(constructRoll().calculateWeight());
    }

}
