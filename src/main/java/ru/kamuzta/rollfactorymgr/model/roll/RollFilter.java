package ru.kamuzta.rollfactorymgr.model.roll;

public enum RollFilter {
    ID("id"),
    SKU("sku"),
    ROLL_TYPE("rollType"),
    PAPER("paper"),
    WIDTH_TYPE("widthType"),
    CORE_TYPE("coreType"),
    MAIN_VALUE("mainValue");

    private String filterName;
    RollFilter(String filterName) {
        this.filterName = filterName;
    }

    @Override
    public String toString() {
        return name();
    }
}
