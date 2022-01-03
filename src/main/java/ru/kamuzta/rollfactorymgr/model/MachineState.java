package ru.kamuzta.rollfactorymgr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MachineState {
    ON(true),
    OFF(false);

    private boolean working;

    @Override
    public String toString() {
        return name();
    }
}
