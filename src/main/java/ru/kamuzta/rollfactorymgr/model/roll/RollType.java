package ru.kamuzta.rollfactorymgr.model.roll;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 *  Roll manufacturing method (with length control or diameter control)
 */
@AllArgsConstructor
@Getter
public enum RollType {
    LENGTH("By length"),
    DIAMETER("By diameter");
    private String typeName;

    @Override
    public String toString() {
        return name();
    }

    public static RollType byTypeName(String typeName) {
        return Arrays.stream(values())
                .filter(rt -> rt.getTypeName().equals(typeName))
                .findFirst()
                .orElseThrow(() -> new EnumConstantNotPresentException(RollType.class,typeName));
    }

    public boolean isLength() {
        return this == LENGTH;
    }

    public boolean isDiameter() {
        return this == DIAMETER;
    }

}
