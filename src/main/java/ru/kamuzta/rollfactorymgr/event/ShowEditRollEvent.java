package ru.kamuzta.rollfactorymgr.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.model.RollProperty;

@AllArgsConstructor
@Getter
public class ShowEditRollEvent extends UIEvent {
    private RollProperty rollProperty;
}
