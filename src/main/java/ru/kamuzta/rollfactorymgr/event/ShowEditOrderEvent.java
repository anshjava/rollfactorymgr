package ru.kamuzta.rollfactorymgr.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.model.order.OrderProperty;

@AllArgsConstructor
@Getter
public class ShowEditOrderEvent extends UIEvent {
    private OrderProperty orderProperty;
}
