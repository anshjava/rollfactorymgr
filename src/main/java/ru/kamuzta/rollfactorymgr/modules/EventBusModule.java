package ru.kamuzta.rollfactorymgr.modules;

import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Module;

public class EventBusModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(EventBus.class).asEagerSingleton();
    }
}
