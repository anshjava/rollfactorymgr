package ru.kamuzta.rollfactorymgr.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;

@AllArgsConstructor
@Getter
public class ShowEditClientEvent extends UIEvent {
    private ClientProperty clientProperty;
}
