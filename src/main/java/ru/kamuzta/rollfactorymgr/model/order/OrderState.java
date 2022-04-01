package ru.kamuzta.rollfactorymgr.model.order;

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
