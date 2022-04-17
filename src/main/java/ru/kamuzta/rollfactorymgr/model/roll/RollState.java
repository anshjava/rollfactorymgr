package ru.kamuzta.rollfactorymgr.model.roll;

public enum RollState {
    ACTIVE,
    REMOVED;

    @Override
    public String toString() {
        return name();
    }
}
