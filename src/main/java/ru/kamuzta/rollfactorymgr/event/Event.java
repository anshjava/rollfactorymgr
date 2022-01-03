package ru.kamuzta.rollfactorymgr.event;

import lombok.extern.slf4j.Slf4j;

/**
 * Event of EventBus
 */
@Slf4j
public class Event {

    public Event() {
        if (!getClass().getName().contains("Key")) {
            log.debug(">>>>>>>> EVENT " + getClass().getName());
        }
    }
}
