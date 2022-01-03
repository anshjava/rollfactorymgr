package ru.kamuzta.rollfactorymgr.model;

import javafx.beans.property.ObjectProperty;
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
    private final StringProperty rollType;
    private final ObjectProperty<BigDecimal> paperWeight;
    private final ObjectProperty<BigDecimal> rollWidth;
    private final ObjectProperty<BigDecimal> coreDiameter;
    private final ObjectProperty<BigDecimal> rollLength;
    private final ObjectProperty<BigDecimal> rollDiameter;
    private final ObjectProperty<BigDecimal> rollWeight;
}
