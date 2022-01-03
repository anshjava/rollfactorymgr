package ru.kamuzta.rollfactorymgr.model;

public enum OrderState {
    NEW,
    QUEUED,
    INPROGRESS,
    COMPLETED;

    @Override
    public String toString() {
        return name();
    }
}
