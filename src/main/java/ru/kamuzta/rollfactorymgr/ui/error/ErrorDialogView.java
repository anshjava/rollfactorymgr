package ru.kamuzta.rollfactorymgr.ui.error;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.kamuzta.rollfactorymgr.event.ScreenEvent;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class ErrorDialogView implements FxmlView<ErrorDialogViewModel>, Initializable {

    @FXML
    private BorderPane mainPanel;
    @FXML
    private TextArea errorMessageField;
    @FXML
    private TextArea errorDetailsField;

    @Getter
    @InjectViewModel
    private ErrorDialogViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessageField.textProperty().bind(getViewModel().errorMessageProperty());
        errorDetailsField.textProperty().bind(getViewModel().errorStackTraceProperty());
        errorDetailsField.setVisible(false);

        Platform.runLater(mainPanel::requestFocus);
    }

    @FXML
    public void onShowHideDetailsPressed(ActionEvent actionEvent) {
        errorDetailsField.setVisible(!errorDetailsField.isVisible());
    }

    @FXML
    public void onClosePressed(ActionEvent actionEvent) {
        close(actionEvent.getTarget());
    }

    private void close(EventTarget target) {
        Event.fireEvent(target, new ScreenEvent(ScreenEvent.SCREEN_CLOSE_REQUEST));
    }
}
