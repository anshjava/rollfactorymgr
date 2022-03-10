package ru.kamuzta.rollfactorymgr.model.roll;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

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

    public static WidthType byWidth(BigDecimal width) {
        return Arrays.stream(values())
                .filter(wt -> wt.getWidth().compareTo(width) == 0)
                .findFirst()
                .orElseThrow(() -> new EnumConstantNotPresentException(WidthType.class, width.toString()));
    }

}
