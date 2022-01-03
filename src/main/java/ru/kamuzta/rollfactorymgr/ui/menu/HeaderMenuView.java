package ru.kamuzta.rollfactorymgr.ui.menu;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderMenuView implements FxmlView<HeaderMenuViewModel>, Initializable {

    @FXML
    private Button exitButton;

    @FXML
    private SplitMenuButton rollSplitMenuButton;
    @FXML
    private SplitMenuButton clientSplitMenuButton;
    @FXML
    private SplitMenuButton orderSplitMenuButton;
    @FXML
    private SplitMenuButton configSplitMenuButton;


    @InjectViewModel
    private HeaderMenuViewModel viewModel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    @FXML
    void onRollRegistry() {
        viewModel.onRollRegistry();
    }

    @FXML
    void onRollFind() {
        viewModel.onRollFind();
    }

    @FXML
    void onRollCreate() {
        viewModel.onRollCreate();
    }

    @FXML
    void onClientRegistry() {
        viewModel.onClientRegistry();
    }

    @FXML
    void onClientFind() {
        viewModel.onClientFind();
    }

    @FXML
    void onClientCreate() {
        viewModel.onClientCreate();
    }

    @FXML
    void onOrderRegistry() {
        viewModel.onOrderRegistry();
    }

    @FXML
    void onOrderFind() {
        viewModel.onOrderFind();
    }

    @FXML
    void onOrderCreate() {
        viewModel.onOrderCreate();
    }

    @FXML
    void onConfigManager() {
        viewModel.onConfigManager();
    }

    @FXML
    void onConfigWorkplace() {
        viewModel.onConfigWorkplace();
    }

    @FXML
    void onConfigConnection() {
        viewModel.onConfigConnection();
    }

    @FXML
    void onAbout() {
        viewModel.onAbout();
    }

    @FXML
    void onExit() {
        viewModel.onExit();
    }
}
