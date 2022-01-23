package ru.kamuzta.rollfactorymgr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * Roll Core
 */
@AllArgsConstructor
@Getter
public enum CoreType {
    CORE_12(BigDecimal.valueOf(12.00)),
    CORE_18(BigDecimal.valueOf(18.00)),
    CORE_26(BigDecimal.valueOf(26.00));

    private BigDecimal diameter;

    @Override
    public String toString() {
        return name();
    }

    public static CoreType byDiameter(BigDecimal diameter) {
        return Arrays.stream(values())
                .filter(ct -> ct.getDiameter().compareTo(diameter) == 0)
                .findFirst()
                .orElseThrow(() -> new EnumConstantNotPresentException(CoreType.class, diameter.toString()));
    }

}
