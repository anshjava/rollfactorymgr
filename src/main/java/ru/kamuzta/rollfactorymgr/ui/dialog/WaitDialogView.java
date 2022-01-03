package ru.kamuzta.rollfactorymgr.ui.dialog;

import com.google.common.base.Strings;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.event.ScreenEvent;
import ru.kamuzta.rollfactorymgr.notifier.NotifierButton;
import ru.kamuzta.rollfactorymgr.ui.KeyBoundButton;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.kamuzta.rollfactorymgr.event.ScreenEvent.SCREEN_CLOSE_REQUEST;

@Slf4j
public class WaitDialogView implements FxmlView<WaitDialogViewModel>, Initializable {

    private static final int SLEEP_BEFORE_CLOSE_MILLIS = 200;

    //for dynamic height conversion
    private static final int BUTTON_HEIGHT = 50;
    private static final int LINE_HEIGHT = 30;
    //base height (side frames)
    private static final int BASE_HEIGHT = 70;

    @FXML
    private Pane interactiveContainer;

    @FXML
    private BorderPane mainPanel;
    @FXML
    private Text messageField;
    @FXML
    public Label countdownLabel;

    @FXML
    private HBox buttonBox;

    @FXML
    private KeyBoundButton yesButton;
    @FXML
    private KeyBoundButton noButton;
    @FXML
    private KeyBoundButton cancelButton;


    @InjectViewModel
    private WaitDialogViewModel viewModel;

    public WaitDialogViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageField.textProperty().bind(getViewModel().messageProperty());
        messageField.setFontSmoothingType(FontSmoothingType.LCD);

        yesButton.managedProperty().bind(yesButton.visibleProperty());
        noButton.managedProperty().bind(noButton.visibleProperty());
        cancelButton.managedProperty().bind(cancelButton.visibleProperty());

        getViewModel().countdownProperty().addListener(
                (observable, oldValue, newValue) ->
                        countdownLabel.setText(newValue.intValue() != 0 ? newValue.toString() : StringUtils.EMPTY));

        // as soon as the operation is completed, close the dialog
        getViewModel().resultProperty().addListener((observable, oldValue, newValue) -> close());

        messageField.textProperty().addListener((observable, oldValue, newValue) -> recalculateHeight());

        buttonBox.managedProperty().bind(buttonBox.visibleProperty());
        buttonBox.visibleProperty().bind(getViewModel().buttonsProperty().emptyProperty().not());

        yesButton.disableProperty().bind(getViewModel().yesButtonDisableProperty());
        noButton.disableProperty().bind(getViewModel().noButtonDisableProperty());
        cancelButton.disableProperty().bind(getViewModel().cancelButtonDisableProperty());

        getViewModel().buttonsProperty().addListener((observable, oldValue, newValue) -> {
            hideNotifierButton(yesButton);
            hideNotifierButton(noButton);
            hideNotifierButton(cancelButton);

            if (!newValue.isEmpty()) {

                for (NotifierButton button : newValue) {
                    switch (button.getPurpose()) {
                        case YES:
                            showNotifierButton(yesButton, button);
                            break;
                        case NO:
                            showNotifierButton(noButton, button);
                            break;
                        case CANCEL:
                            showNotifierButton(cancelButton, button);
                            break;
                    }

                }
            }
            recalculateHeight();
        });

        //container for inline element
        getViewModel().interactParentProperty().addListener((observable, oldValue, newValue) -> {
            interactiveContainer.getChildren().clear();
            if (newValue != null) {
                interactiveContainer.getChildren().add(newValue);
            }
            recalculateHeight();
        });
    }

    private void recalculateHeight() {
        if (viewModel.getRootDialog() == null) {
            mainPanel.setMaxHeight(150);
            return;
        }

        int res = BASE_HEIGHT;
        if (!getViewModel().buttonsProperty().isEmpty()) {
            res += BUTTON_HEIGHT;
        }

        int lineCount = messageField.getText().split("\n").length;

        if (!interactiveContainer.getChildren().isEmpty()) {
            Node node = interactiveContainer.getChildren().get(0);
            if (node instanceof Region) {
                res += ((Region) node).getPrefHeight();
            }
        } else {
            lineCount = Math.max(lineCount, 2);
        }

        res += lineCount * LINE_HEIGHT;

        viewModel.getRootDialog().setHeight(res);
    }

    private void hideNotifierButton(KeyBoundButton button) {
        button.setVisible(false);
    }

    private void showNotifierButton(KeyBoundButton button, NotifierButton nb) {
        button.setText(nb.getText());
        button.onActionProperty().setValue(actionEvent -> {
            nb.getAction().run();
            if (!Strings.isNullOrEmpty(nb.getAfterActionMessage())) {
                getViewModel().messageProperty().set(nb.getAfterActionMessage());
            }
        });
        button.setVisible(true);
    }

    private void close() {
        sleepBeforeClose();
        Event.fireEvent(mainPanel, new ScreenEvent(SCREEN_CLOSE_REQUEST).withResult(viewModel.getResult()));
    }

    /**
     * a method introduced so that the dialog does not flash ugly when a task is performed too quickly
     */
    private void sleepBeforeClose() {
        try {
            Thread.sleep(SLEEP_BEFORE_CLOSE_MILLIS);
        } catch (InterruptedException e) {
            log.error("Error while sleeping: ", e);
            Thread.currentThread().interrupt();
        }
    }
}
