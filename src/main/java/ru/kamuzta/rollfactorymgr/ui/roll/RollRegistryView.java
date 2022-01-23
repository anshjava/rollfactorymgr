package ru.kamuzta.rollfactorymgr.ui.roll;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import ru.kamuzta.rollfactorymgr.model.*;
import ru.kamuzta.rollfactorymgr.ui.menu.HeaderMenuView;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class RollRegistryView implements FxmlView<RollRegistryViewModel>, Initializable {

    @FXML
    public BorderPane headerPane;

    @FXML
    private HeaderMenuView headerMenuView;

    @FXML
    private Button updateRollRegistryButton;

    @FXML
    private Button editRollButton;

    @FXML
    private Button removeRollButton;

    @FXML
    private TableView<RollProperty> rollRegistryTableView;
    @FXML
    private TableColumn<RollProperty, Long> idColumn;
    @FXML
    private TableColumn<RollProperty, String> skuColumn;
    @FXML
    private TableColumn<RollProperty, String> typeColumn;
    @FXML
    private TableColumn<RollProperty, BigDecimal> paperColumn;
    @FXML
    private TableColumn<RollProperty, BigDecimal> widthColumn;
    @FXML
    private TableColumn<RollProperty, BigDecimal> coreColumn;
    @FXML
    private TableColumn<RollProperty, BigDecimal> lengthColumn;
    @FXML
    private TableColumn<RollProperty, BigDecimal> diameterColumn;
    @FXML
    private TableColumn<RollProperty, BigDecimal> weightColumn;

    @InjectViewModel
    private RollRegistryViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //update roll registry
        viewModel.onUpdateRollRegistry();
        //configure table
        rollRegistryTableView.itemsProperty().bind(viewModel.rollPropertiesProperty());
        idColumn.setCellValueFactory(column -> column.getValue().getId());
        skuColumn.setCellValueFactory(column -> column.getValue().getSku());
        typeColumn.setCellValueFactory(column -> column.getValue().getRollType());
        paperColumn.setCellValueFactory(column -> column.getValue().getPaperWeight());
        widthColumn.setCellValueFactory(column -> column.getValue().getRollWidth());
        coreColumn.setCellValueFactory(column -> column.getValue().getCoreDiameter());
        lengthColumn.setCellValueFactory(column -> column.getValue().getRollLength());
        diameterColumn.setCellValueFactory(column -> column.getValue().getRollDiameter());
        weightColumn.setCellValueFactory(column -> column.getValue().getRollWeight());
        //configure buttons
        editRollButton.visibleProperty().bind(rollRegistryTableView.getSelectionModel().selectedIndexProperty().greaterThan(-1));
        editRollButton.managedProperty().bind(editRollButton.visibleProperty());
        removeRollButton.visibleProperty().bind(editRollButton.visibleProperty());
        removeRollButton.managedProperty().bind(removeRollButton.visibleProperty());
    }

    @FXML
    void onUpdateRollRegistry() {
        viewModel.onUpdateRollRegistry();
    }

    @FXML
    void onEditRoll() {
        int selectedIndex = rollRegistryTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onEditRoll(rollRegistryTableView.getItems().get(selectedIndex));
        }
    }

    @FXML
    void onRemoveRoll() {
        int selectedIndex = rollRegistryTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onRemoveRoll(rollRegistryTableView.getItems().get(selectedIndex).getSku().getValue());
        }
    }

}
