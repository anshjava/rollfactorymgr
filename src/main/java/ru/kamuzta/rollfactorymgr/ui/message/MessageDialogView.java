package ru.kamuzta.rollfactorymgr.ui.message;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.kamuzta.rollfactorymgr.event.ScreenEvent;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MessageDialogView implements FxmlView<MessageDialogViewModel>, Initializable {

    private static final String IMP_LABEL_STYLE = "imp-label";

    @FXML
    private BorderPane mainPanel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Button choiceOneButton;
    @FXML
    private Button choiceTwoButton;
    @FXML
    private Hyperlink errorHyperLink;
    @FXML
    private HBox moreDetailsBox;
    @FXML
    private Label moreDetailsLabel;

    @InjectViewModel
    private MessageDialogViewModel viewModel;

    public MessageDialogViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.textProperty().bind(getViewModel().titleProperty());
        messageLabel.textProperty().bind(getViewModel().messageProperty());
        choiceOneButton.textProperty().bind(getViewModel().choiceOneActionNameProperty());
        choiceOneButton.disableProperty().bind(getViewModel().choiceOneDisabledProperty());

        choiceTwoButton.textProperty().bind(getViewModel().choiceTwoActionNameProperty());
        choiceTwoButton.visibleProperty().bind(Bindings.not(getViewModel().choiceTwoActionNameProperty().isEmpty()));
        choiceTwoButton.disableProperty().bind(getViewModel().choiceTwoDisabledProperty());

        moreDetailsBox.visibleProperty().bind(getViewModel().hyperlinkText().isNotEmpty());
        moreDetailsBox.managedProperty().bind(getViewModel().hyperlinkText().isNotEmpty());
        moreDetailsLabel.textProperty().setValue("Show more...");
        errorHyperLink.textProperty().bind(getViewModel().hyperlinkText());

        viewModel.importantProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                messageLabel.getStyleClass().add(IMP_LABEL_STYLE);
            }
        });
    }

    public void onChoiceOnePressed(ActionEvent actionEvent) {
        getViewModel().onChoiceOne();
        close(actionEvent);
    }

    public void onChoiceTwoPressed(ActionEvent actionEvent) {
        getViewModel().onChoiceTwo();
        close(actionEvent);
    }

    private void close(ActionEvent actionEvent) {
        Event.fireEvent(actionEvent.getTarget(), new ScreenEvent(ScreenEvent.SCREEN_CLOSE_REQUEST).withResult(viewModel.getResult()));
    }

    public Button getChoiceOneButton() {
        return choiceOneButton;
    }

    public Button getChoiceTwoButton() {
        return choiceTwoButton;
    }

    @FXML
    private void onManualReferenceClick() {
        new Thread(() -> {
            try {
                Desktop.getDesktop().browse(new URI("http://google.com"));
            } catch (IOException | URISyntaxException e) {
                //todo
            }
        }).start();
    }
}
