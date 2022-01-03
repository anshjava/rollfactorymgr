package ru.kamuzta.rollfactorymgr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

}
