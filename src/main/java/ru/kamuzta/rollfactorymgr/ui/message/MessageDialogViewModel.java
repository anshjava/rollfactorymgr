package ru.kamuzta.rollfactorymgr.ui.message;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCodeCombination;
import ru.kamuzta.rollfactorymgr.event.MessageDialogOpenEvent;
import ru.kamuzta.rollfactorymgr.exception.ExceptionalRunnable;
import ru.kamuzta.rollfactorymgr.ui.dialog.MessageDialogAction;
import ru.kamuzta.rollfactorymgr.utils.exception.ExceptionUtils;

import java.util.Optional;

public class MessageDialogViewModel implements ViewModel {

    private final EventBus eventBus;
    private StringProperty title = new SimpleStringProperty();
    private StringProperty message = new SimpleStringProperty();
    private StringProperty choiceOneActionName = new SimpleStringProperty();
    private StringProperty choiceTwoActionName = new SimpleStringProperty();
    private BooleanProperty choiceOneDisabled = new SimpleBooleanProperty(false);
    private BooleanProperty choiceTwoDisabled = new SimpleBooleanProperty(false);
    private final BooleanProperty important = new SimpleBooleanProperty(false);

    private Optional<ExceptionalRunnable> choiceOneOperationHolder = Optional.empty();
    private Optional<ExceptionalRunnable> choiceTwoOperationHolder = Optional.empty();

    private Optional<ButtonType> choiceOneButtonTypeHolder = Optional.empty();
    private Optional<ButtonType> choiceTwoButtonTypeHolder = Optional.empty();

    private ObjectProperty<KeyCodeCombination> choiceOneKeyBinding = new SimpleObjectProperty<>();
    private ObjectProperty<KeyCodeCombination> choiceTwoKeyBinding = new SimpleObjectProperty<>();
    private ObjectProperty<ButtonType> result = new SimpleObjectProperty<>();
    private final StringProperty hyperLinkText = new SimpleStringProperty();
    @Inject
    public MessageDialogViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void onMessageOpen(MessageDialogOpenEvent event) {
        setTitle(event.getTitle());
        setMessage(event.getMessage());

        MessageDialogAction choiceOneAction = event.getChoiceOneAction();
        Optional<MessageDialogAction> choiceTwoActionHolder = event.getChoiceTwoActionHolder();

        setChoiceOneActionName(choiceOneAction.getName());
        setChoiceOneDisabled(event.getChoiceOneAction().isDisabled());
        choiceOneOperationHolder = Optional.ofNullable(choiceOneAction.getOperation());
        choiceOneButtonTypeHolder = Optional.ofNullable(choiceOneAction.getButtonType());
        setChoiceOneKeyBinding(choiceOneAction.getKeyBinding());
        setHyperlinkText(event.getHyperLinkText());

        important.setValue(event.isImportant());

        choiceTwoActionHolder.ifPresent(action -> {
            setChoiceTwoActionName(action.getName());
            setChoiceTwoDisabled(action.isDisabled());
            choiceTwoOperationHolder = Optional.ofNullable(action.getOperation());
            choiceTwoButtonTypeHolder = Optional.ofNullable(action.getButtonType());
            setChoiceTwoKeyBinding(action.getKeyBinding());
        });

    }

    public void onChoiceOne() {
        onChoice(choiceOneOperationHolder, choiceOneButtonTypeHolder);
    }

    public void onChoiceTwo() {
        onChoice(choiceTwoOperationHolder, choiceTwoButtonTypeHolder);
    }

    private void onChoice(Optional<ExceptionalRunnable> operationHolder, Optional<ButtonType> buttonTypeHolder) {
        operationHolder.ifPresent(operation -> {
            try {
                operation.run();
            } catch (Exception e) {
                throw ExceptionUtils.wrapNotRuntimeException(e);
            }
        });

        ButtonType resultButtonType = buttonTypeHolder.isPresent() ? buttonTypeHolder.get() : ButtonType.CLOSE;

        setResult(resultButtonType);
    }

    // ===============================================================================================================

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public String getChoiceOneActionName() {
        return choiceOneActionName.get();
    }

    public StringProperty choiceOneActionNameProperty() {
        return choiceOneActionName;
    }

    public void setChoiceOneActionName(String choiceOneActionName) {
        this.choiceOneActionName.set(choiceOneActionName);
    }

    public String getChoiceTwoActionName() {
        return choiceTwoActionName.get();
    }

    public StringProperty choiceTwoActionNameProperty() {
        return choiceTwoActionName;
    }

    public void setChoiceTwoActionName(String choiceTwoActionName) {
        this.choiceTwoActionName.set(choiceTwoActionName);
    }

    public KeyCodeCombination getChoiceOneKeyBinding() {
        return choiceOneKeyBinding.get();
    }

    public ObjectProperty<KeyCodeCombination> choiceOneKeyBindingProperty() {
        return choiceOneKeyBinding;
    }

    public void setChoiceOneKeyBinding(KeyCodeCombination choiceOneKeyBinding) {
        this.choiceOneKeyBinding.set(choiceOneKeyBinding);
    }

    public KeyCodeCombination getChoiceTwoKeyBinding() {
        return choiceTwoKeyBinding.get();
    }

    public ObjectProperty<KeyCodeCombination> choiceTwoKeyBindingProperty() {
        return choiceTwoKeyBinding;
    }

    public void setChoiceTwoKeyBinding(KeyCodeCombination choiceTwoKeyBinding) {
        this.choiceTwoKeyBinding.set(choiceTwoKeyBinding);
    }

    public ButtonType getResult() {
        return result.get();
    }

    public ObjectProperty<ButtonType> resultProperty() {
        return result;
    }

    public void setResult(ButtonType result) {
        this.result.set(result);
    }

    public BooleanProperty importantProperty() {
        return important;
    }

    public boolean isChoiceOneDisabled() {
        return choiceOneDisabled.get();
    }

    public BooleanProperty choiceOneDisabledProperty() {
        return choiceOneDisabled;
    }

    public void setChoiceOneDisabled(boolean choiceOneDisabled) {
        this.choiceOneDisabled.set(choiceOneDisabled);
    }

    public boolean isChoiceTwoDisabled() {
        return choiceTwoDisabled.get();
    }

    public BooleanProperty choiceTwoDisabledProperty() {
        return choiceTwoDisabled;
    }

    public void setChoiceTwoDisabled(boolean choiceTwoDisabled) {
        this.choiceTwoDisabled.set(choiceTwoDisabled);
    }

    public StringProperty hyperlinkText() {
        return hyperLinkText;
    }

    public String getHyperlinkText() {
        return hyperLinkText.get();
    }

    public void setHyperlinkText(String hyperlinkText) {
        this.hyperLinkText.set(hyperlinkText);
    }
}
