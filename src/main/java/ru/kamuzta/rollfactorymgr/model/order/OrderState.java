package ru.kamuzta.rollfactorymgr.model.order;

public enum OrderState {
    NEW,
    QUEUED,
    INPROGRESS,
    COMPLETED,
    CANCELED;

    @Override
    public String toString() {
        return name();
    }
}
