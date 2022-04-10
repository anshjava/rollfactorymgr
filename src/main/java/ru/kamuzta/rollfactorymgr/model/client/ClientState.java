package ru.kamuzta.rollfactorymgr.model.client;

public enum ClientState {
    ACTIVE,
    DELETED;

    @Override
    public String toString() {
        return name();
    }
}
