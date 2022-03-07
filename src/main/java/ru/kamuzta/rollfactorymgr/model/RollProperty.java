package ru.kamuzta.rollfactorymgr.model;

import javafx.beans.property.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
        return new RollProperty(new SimpleObjectProperty<>(null),
                new SimpleStringProperty(""),
                new SimpleObjectProperty<>(RollType.LENGTH),
                new SimpleObjectProperty<>(Paper.NTC44),
                new SimpleObjectProperty<>(WidthType.WIDTH_57),
                new SimpleObjectProperty<>(CoreType.CORE_12),
                new SimpleObjectProperty<>(null)
        );
    }

    private Roll constructRoll() {
        return new Roll(Optional.ofNullable(id.getValue()).orElse(0L),
                sku.getValue(),
                rollType.getValue(),
                paper.getValue(),
                widthType.getValue(),
                coreType.getValue(),
                Optional.ofNullable(mainValue.getValue()).orElse(BigDecimal.ZERO));
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

    @Override
    public RollProperty clone() {
        return new RollProperty(new SimpleObjectProperty<>(id.getValue()),
                new SimpleStringProperty(sku.getValue()),
                new SimpleObjectProperty<>(rollType.getValue()),
                new SimpleObjectProperty<>(paper.getValue()),
                new SimpleObjectProperty<>(widthType.getValue()),
                new SimpleObjectProperty<>(coreType.getValue()),
                new SimpleObjectProperty<>(mainValue.getValue())
        );
    }

}
