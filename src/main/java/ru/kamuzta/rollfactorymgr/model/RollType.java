package ru.kamuzta.rollfactorymgr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

}
