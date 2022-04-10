package ru.kamuzta.rollfactorymgr.model.roll;

public enum RollState {
    ACTIVE,
    DELETED;

    @Override
    public String toString() {
        return name();
    }
}
