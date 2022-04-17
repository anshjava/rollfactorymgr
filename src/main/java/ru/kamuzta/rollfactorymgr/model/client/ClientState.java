package ru.kamuzta.rollfactorymgr.model.client;

public enum ClientState {
    ACTIVE,
    REMOVED;

    @Override
    public String toString() {
        return name();
    }
}
