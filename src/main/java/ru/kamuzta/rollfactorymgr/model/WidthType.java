package ru.kamuzta.rollfactorymgr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Width of Roll
 */
@AllArgsConstructor
@Getter
public enum WidthType {
    WIDTH_57(BigDecimal.valueOf(57.00)),
    WIDTH_80(BigDecimal.valueOf(80.00));

    private BigDecimal width;

    @Override
    public String toString() {
        return name();
    }

}
