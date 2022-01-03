package ru.kamuzta.rollfactorymgr.ui.menu;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuView implements FxmlView<MenuViewModel>, Initializable {
    @FXML
    public BorderPane headerPane;

    @FXML
    private HeaderMenuView headerMenuView;

    @FXML
    private ImageView backgroundImage;

    @InjectViewModel
    private MenuViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backgroundImage.setFitHeight(550);
        backgroundImage.setFitWidth(550);
    }
}
