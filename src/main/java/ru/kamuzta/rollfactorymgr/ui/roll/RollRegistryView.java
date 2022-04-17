package ru.kamuzta.rollfactorymgr.ui.roll;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.ui.menu.HeaderMenuView;
import ru.kamuzta.rollfactorymgr.ui.table.*;

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
    private TableColumn<RollProperty, RollType> typeColumn;
    @FXML
    private TableColumn<RollProperty, Paper> paperColumn;
    @FXML
    private TableColumn<RollProperty, WidthType> widthColumn;
    @FXML
    private TableColumn<RollProperty, CoreType> coreColumn;
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

        typeColumn.setCellFactory(column -> new RollTypeCell<>());
        typeColumn.setCellValueFactory(column -> column.getValue().getRollType());

        paperColumn.setCellFactory(column -> new PaperCell<>());
        paperColumn.setCellValueFactory(column -> column.getValue().getPaper());

        widthColumn.setCellFactory(column -> new WidthTypeCell<>());
        widthColumn.setCellValueFactory(column -> column.getValue().getWidthType());

        coreColumn.setCellFactory(column -> new CoreTypeCell<>());
        coreColumn.setCellValueFactory(column -> column.getValue().getCoreType());

        lengthColumn.setCellValueFactory(column -> {
            if (column.getValue().getRollType().get().isLength()) {
                return column.getValue().getMainValue();
            } else {
                return column.getValue().calculateLength();
            }
        });
        lengthColumn.setCellFactory(column -> new BigDecimalCell<>(1));

        diameterColumn.setCellValueFactory(column -> {
            if (column.getValue().getRollType().get().isDiameter()) {
                return column.getValue().getMainValue();
            } else {
                return column.getValue().calculateDiameter();
            }
        });
        diameterColumn.setCellFactory(column -> new BigDecimalCell<>(0));

        weightColumn.setCellValueFactory(column -> column.getValue().calculateWeight());
        weightColumn.setCellFactory(column -> new BigDecimalCell<>(3));

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
            viewModel.onEditRoll(new RollProperty(rollRegistryTableView.getItems().get(selectedIndex)));
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
