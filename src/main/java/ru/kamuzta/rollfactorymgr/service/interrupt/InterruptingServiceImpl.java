package ru.kamuzta.rollfactorymgr.service.interrupt;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.notifier.NotificationEvent;
import ru.kamuzta.rollfactorymgr.notifier.NotifierButton;
import ru.kamuzta.rollfactorymgr.ui.ValueChangeListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;


@Singleton
@Slf4j
public class InterruptingServiceImpl implements InterruptingService {
    private final Map<Long, Deque<Runnable>> actions = new ConcurrentHashMap<>();
    private final Set<ValueChangeListener<Boolean>> listeners = new LinkedHashSet<>();
    private AtomicBoolean empty = new AtomicBoolean(true);

    @Override
    public boolean hasActiveInterruptActions() {
        return !empty.get();
    }

    @Override
    public void addHasActiveInterruptListener(@NotNull ValueChangeListener<Boolean> listener) {
        listeners.add(listener);
        fireListener(listener);
    }

    @Override
    public boolean removeListener(@NotNull ValueChangeListener<Boolean> listener) {
        return listeners.remove(listener);
    }

    @Override
    public void registerInterruptAction(@NotNull Runnable interruptAction) {
        getDeque(Thread.currentThread().getId()).addLast(interruptAction);
        updateEmpty();
    }

    @Override
    public boolean removeInterruptAction(@NotNull Runnable interruptAction) {
        boolean result = actions.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(action -> action.equals(interruptAction));
        actions.values().forEach(deque -> deque.remove(interruptAction));
        removeEmpty();
        return result;
    }

    @Override
    public void fireInterrupt() {
        if (empty.get()) {
            return;
        }
        empty.set(true);
        fireListeners();
        actions.values().forEach(this::applyActions);
        removeEmpty();
    }

    @Override
    public NotificationEvent createNotificationEvent(String message, String buttonText, String afterInterruptMessage) {
        return new NotificationEvent(
                this,
                message,
                0,
                0,
                Collections.singletonList(new NotifierButton(
                        NotifierButton.Purpose.CANCEL,
                        buttonText,
                        this::fireInterrupt,
                        afterInterruptMessage
                )),
                null,
                true);
    }

    @NotNull
    private Deque<Runnable> getDeque(long threadId) {
        return actions.computeIfAbsent(threadId, id -> new ConcurrentLinkedDeque<>());
    }

    private void applyActions(Deque<Runnable> actions) {
        while (!actions.isEmpty()) {
            try {
                actions.removeLast().run();
            } catch (Exception e) {
                log.error("Error interrupting action " + e.getMessage(), e);
            }
        }
    }

    private void removeEmpty() {
        new ArrayList<>(actions.keySet()).forEach(key -> {
            if (actions.get(key).isEmpty()) {
                actions.remove(key);
            }
        });
        updateEmpty();
    }

    private void updateEmpty() {
        boolean newValue = actions.isEmpty();
        if (newValue != empty.get()) {
            empty.set(newValue);
            fireListeners();
        }
    }

    private void fireListeners() {
        try {
            listeners.forEach(this::fireListener);
        } catch (Exception e) {
            log.error("Error firing empty changed", e);
        }
    }

    private void fireListener(ValueChangeListener<Boolean> listener) {
        listener.valueChanged(hasActiveInterruptActions());
    }
}