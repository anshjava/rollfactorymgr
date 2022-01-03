package ru.kamuzta.rollfactorymgr.ui.dialog;


import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import ru.kamuzta.rollfactorymgr.ui.main.MainView;

public class DialogAlert extends Alert {
    /**
     * toBeClosed means - should the dialog be closed
     * introduced to resolve the situation when called completeDialog
     * before showAndWait
     * now if showAndWait is called after setResult(), the dialog will not open
     */
    private boolean toBeClosed = false;

    public DialogAlert() {
        super(AlertType.NONE);

        initStyle(StageStyle.TRANSPARENT);
        getDialogPane().getStyleClass().clear();
        getDialogPane().getStyleClass().add(MainView.ROOT_CSS_CLASS);

        setOnShown(event -> createOnShownAction());
    }

    protected void createOnShownAction() {
        if (toBeClosed) {
            Platform.runLater(this::close);
        }
    }

    public void completeDialog(ButtonType result) {
        setResult(result);
        toBeClosed = true;
    }
}
