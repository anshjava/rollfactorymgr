package ru.kamuzta.rollfactorymgr.ui.keybinding;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.event.KeyBindingCreateEvent;
import ru.kamuzta.rollfactorymgr.event.KeyBindingRemoveEvent;

import java.util.Map;

@Slf4j
@Singleton
public class KeyBindingUtil {

    @Inject
    private static EventBus eventBus;

    public static final ImmutableMap<String, String> DISPLAY_TEXT_REPLACE_MAP = ImmutableMap.<String, String>builder()
            .put("Escape", KeyCode.ESCAPE.getName()) // Escape -> Esc
            .put("↵", KeyCode.ENTER.getName()) // ↵ -> Enter
            .build();

    public static void fireCreateKeyBindingEvent(Node node, @NotNull KeyCodeCombination keyBinding, @NotNull KeyBoundAction action, boolean common) {
        eventBus.post(new KeyBindingCreateEvent(node, keyBinding, action, common));
    }

    public static void fireCreateKeyBindingEvent(Node node, @NotNull KeyCode keyCode, @NotNull KeyBoundAction action, boolean common) {
        fireCreateKeyBindingEvent(node, new KeyCodeCombination(keyCode), action, common);
    }

    public static void fireRemoveKeyBindingEvent(Node node, @NotNull KeyCodeCombination keyBinding, boolean common) {
        eventBus.post(new KeyBindingRemoveEvent(node, keyBinding, common));
    }

    public static void fireRemoveKeyBindingEvent(Node node, @NotNull KeyCode keyCode, boolean common) {
        fireRemoveKeyBindingEvent(node, new KeyCodeCombination(keyCode), common);
    }

    /**
     * @param keyBinding hotket
     * @return mark
     * useed to improve {@link KeyCodeCombination#getDisplayText()}
     */
    @NotNull
    public static String getDisplayTextImproved(KeyCodeCombination keyBinding) {
        if (keyBinding == null) {
            return StringUtils.EMPTY;
        }

        String improvedText = keyBinding.getDisplayText();

        for (Map.Entry<String, String> entry : DISPLAY_TEXT_REPLACE_MAP.entrySet()) {
            improvedText = improvedText.replace(entry.getKey(), entry.getValue());
        }

        return improvedText;
    }

    public static void executeWhenScenePropertyNotNull(Node node, Runnable operation) {
        if (node.getScene() != null) {
            operation.run();
        } else {
            node.sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                    observable.removeListener(this);
                    if (newValue != null) {
                        operation.run();
                    }
                }
            });
        }
    }
}

