package ru.kamuzta.rollfactorymgr.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Screen {
    ANY,

    ROLL_REGISTRY(true),

    ROLL_EDIT,

    ROLL_FIND,

    ROLL_CREATE,

    CLIENT_REGISTRY(true),

    CLIENT_FIND,

    CLIENT_CREATE,

    ERROR_DIALOG,

    HEADER_MENU,

    ORDER_REGISTRY(true),

    ORDER_FIND,

    ORDER_CREATE,

    CONFIG_MANAGER,

    CONFIG_WORKPLACE,

    CONFIG_CONNECTION,

    ABOUT,

    MENU,

    MESSAGE_DIALOG,

    NOTIFICATION,

    UNDERLAY,

    WAIT_DIALOG;

    private boolean fullScreen;
}
