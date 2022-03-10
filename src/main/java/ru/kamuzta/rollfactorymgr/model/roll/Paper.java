package ru.kamuzta.rollfactorymgr.model.roll;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Raw materials (paper) with parameters
 */
@AllArgsConstructor
@Getter
public enum Paper {
    NTC44("NTC44", "Hansol", "NTC ST 44", BigDecimal.valueOf(44.00), BigDecimal.valueOf(49.00)),
    NTC48("NTC48", "Hansol", "NTC ST 48", BigDecimal.valueOf(48.00), BigDecimal.valueOf(52.00)),
    NTC55("NTC55", "Hansol", "NTC ST 55", BigDecimal.valueOf(55.00), BigDecimal.valueOf(59.00)),
    NTC58("NTC58", "Hansol", "NTC STH 58", BigDecimal.valueOf(58.00), BigDecimal.valueOf(71.00));


    private String code;
    private String manufacturer;
    private String sort;
    private BigDecimal weight;
    private BigDecimal thickness;

    @Override
    public String toString() {
        return name();
    }

    public static Paper byWeight(BigDecimal weight) {
        return Arrays.stream(values())
                .filter(p -> p.getWeight().compareTo(weight) == 0)
                .findFirst()
                .orElseThrow(() -> new EnumConstantNotPresentException(Paper.class, weight.toString()));
    }
}


