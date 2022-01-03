package ru.kamuzta.rollfactorymgr.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDialogOpenEvent extends UIEvent {
    private final Throwable throwable;
    private final String message;

    public ErrorDialogOpenEvent(Throwable throwable) {
        this(throwable, null);
    }
}
