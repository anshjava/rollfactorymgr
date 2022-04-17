package ru.kamuzta.rollfactorymgr.ui.roll;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.ui.menu.HeaderMenuView;
import ru.kamuzta.rollfactorymgr.ui.table.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RollFindView implements FxmlView<RollFindViewModel>, Initializable {

    @FXML
    public BorderPane headerPane;

    @FXML
    private HeaderMenuView headerMenuView;

    @FXML
    private TitledPane filtersPane;

    @FXML
    private TitledPane resultPane;

    @FXML
    private ListView<RollFilter> availableFilters;

    @FXML
    private ListView<RollFilter> selectedFilters;

    @FXML
    TextField id;
    @FXML
    TextField sku;
    @FXML
    ComboBox<RollType> rollType;
    @FXML
    ComboBox<Paper> paper;
    @FXML
    ComboBox<WidthType> widthType;
    @FXML
    ComboBox<CoreType> coreType;
    @FXML
    TextField mainValue;

    @FXML
    Label idLabel;
    @FXML
    Label skuLabel;
    @FXML
    Label rollTypeLabel;
    @FXML
    Label paperLabel;
    @FXML
    Label widthTypeLabel;
    @FXML
    Label coreTypeLabel;
    @FXML
    Label mainValueLabel;

    @FXML
    private Button findRollButton;
    @FXML
    private Button editRollButton;
    @FXML
    private Button removeRollButton;

    @FXML
    private TableView<RollProperty> resultTableView;
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
    private RollFindViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureSelectFilterArea();
        configureFilterValues();
        configureInitialFiltersVisibility();
        configureTable();

        //configure buttons
        findRollButton.disableProperty().set(true);

        editRollButton.visibleProperty().bind(resultTableView.getSelectionModel().selectedIndexProperty().greaterThan(-1));
        editRollButton.managedProperty().bind(editRollButton.visibleProperty());

        removeRollButton.visibleProperty().bind(editRollButton.visibleProperty());
        removeRollButton.managedProperty().bind(removeRollButton.visibleProperty());
    }

    private void configureSelectFilterArea() {
        //configure filters
        availableFilters.itemsProperty().bindBidirectional(viewModel.getAvailableFilters());
        availableFilters.setOnMouseClicked(event -> {
            RollFilter clickedFilter = availableFilters.getSelectionModel().getSelectedItem();
            if (clickedFilter != null) {
                selectedFilters.getItems().add(clickedFilter);
                selectedFilters.getSelectionModel().clearSelection();
                availableFilters.getItems().remove(clickedFilter);
                availableFilters.getSelectionModel().clearSelection();
                refreshFilterVisibility(selectedFilters.getItems());
                reCalculateResultHeight();
            }
        });

        selectedFilters.itemsProperty().bindBidirectional(viewModel.getSelectedFilters());
        selectedFilters.setOnMouseClicked(event -> {
            RollFilter clickedFilter = selectedFilters.getSelectionModel().getSelectedItem();
            if (clickedFilter != null) {
                availableFilters.getItems().add(clickedFilter);
                availableFilters.getSelectionModel().clearSelection();
                selectedFilters.getItems().remove(clickedFilter);
                selectedFilters.getSelectionModel().clearSelection();
                refreshFilterVisibility(selectedFilters.getItems());
                reCalculateResultHeight();
            }
        });
    }

    private void configureTable() {
        //configure table
        resultTableView.itemsProperty().bind(viewModel.rollPropertiesProperty());
        resultTableView.minHeightProperty().bind(resultTableView.prefHeightProperty());
        resultTableView.maxHeightProperty().bind(resultTableView.prefHeightProperty());

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
    }


    private void configureFilterValues() {
        rollType.setItems(FXCollections.observableArrayList(RollType.values()));
        rollType.setConverter(new StringConverter<RollType>() {
            @Override
            public String toString(RollType rollType) {
                return rollType.getTypeName();
            }

            @Override
            public RollType fromString(String typeName) {
                return RollType.byTypeName(typeName);
            }
        });
        paper.setItems(FXCollections.observableArrayList(Paper.values()));
        paper.setConverter(new StringConverter<Paper>() {
            @Override
            public String toString(Paper paper) {
                return paper.getSort();
            }

            @Override
            public Paper fromString(String weight) {
                return Paper.byWeight(new BigDecimal(weight));
            }
        });
        widthType.setItems(FXCollections.observableArrayList(WidthType.values()));
        widthType.setConverter(new StringConverter<WidthType>() {
            @Override
            public String toString(WidthType widthType) {
                return widthType.getWidth().toString();
            }

            @Override
            public WidthType fromString(String width) {
                return WidthType.byWidth(new BigDecimal(width));
            }
        });
        coreType.setItems(FXCollections.observableArrayList(CoreType.values()));
        coreType.setConverter(new StringConverter<CoreType>() {
            @Override
            public String toString(CoreType coreType) {
                return coreType.getDiameter().toString();
            }

            @Override
            public CoreType fromString(String diameter) {
                return CoreType.byDiameter(new BigDecimal(diameter));
            }
        });

        RollProperty rollProperty = viewModel.getRollProperty();
        id.textProperty().addListener((observable, oldValue, newValue) -> {
            Long newLongValue;
            try {
                newLongValue = Long.valueOf(newValue);
            } catch (NumberFormatException nfe) {
                newLongValue = null;
            }
            rollProperty.getId().setValue(newLongValue);
        });
        sku.textProperty().addListener((observable, oldValue, newValue) -> rollProperty.getSku().setValue(newValue));
        rollType.valueProperty().bindBidirectional(rollProperty.getRollType());
        paper.valueProperty().bindBidirectional(rollProperty.getPaper());
        widthType.valueProperty().bindBidirectional(rollProperty.getWidthType());
        coreType.valueProperty().bindBidirectional(rollProperty.getCoreType());
        mainValue.textProperty().addListener((observable, oldValue, newValue) -> rollProperty.getMainValue().setValue(new BigDecimal(newValue)));
    }

    private void configureInitialFiltersVisibility() {
        id.visibleProperty().set(false);
        id.managedProperty().bind(id.visibleProperty());
        idLabel.visibleProperty().bind(id.visibleProperty());
        idLabel.managedProperty().bind(idLabel.visibleProperty());

        sku.visibleProperty().set(false);
        sku.managedProperty().bind(sku.visibleProperty());
        skuLabel.visibleProperty().bind(sku.visibleProperty());
        skuLabel.managedProperty().bind(skuLabel.visibleProperty());

        rollType.visibleProperty().set(false);
        rollType.managedProperty().bind(rollType.visibleProperty());
        rollTypeLabel.visibleProperty().bind(rollType.visibleProperty());
        rollTypeLabel.managedProperty().bind(rollTypeLabel.visibleProperty());

        paper.visibleProperty().set(false);
        paper.managedProperty().bind(paper.visibleProperty());
        paperLabel.visibleProperty().bind(paper.visibleProperty());
        paperLabel.managedProperty().bind(paperLabel.visibleProperty());

        widthType.visibleProperty().set(false);
        widthType.managedProperty().bind(widthType.visibleProperty());
        widthTypeLabel.visibleProperty().bind(widthType.visibleProperty());
        widthTypeLabel.managedProperty().bind(widthTypeLabel.visibleProperty());

        coreType.visibleProperty().set(false);
        coreType.managedProperty().bind(coreType.visibleProperty());
        coreTypeLabel.visibleProperty().bind(coreType.visibleProperty());
        coreTypeLabel.managedProperty().bind(coreTypeLabel.visibleProperty());

        mainValue.visibleProperty().set(false);
        mainValue.managedProperty().bind(mainValue.visibleProperty());
        mainValueLabel.visibleProperty().bind(mainValue.visibleProperty());
        mainValueLabel.managedProperty().bind(mainValueLabel.visibleProperty());
    }

    private void refreshFilterVisibility(ObservableList<RollFilter> rollFilters) {
        id.visibleProperty().set(rollFilters.contains(RollFilter.ID));
        sku.visibleProperty().set(rollFilters.contains(RollFilter.SKU));
        rollType.visibleProperty().set(rollFilters.contains(RollFilter.ROLL_TYPE));
        paper.visibleProperty().set(rollFilters.contains(RollFilter.PAPER));
        widthType.visibleProperty().set(rollFilters.contains(RollFilter.WIDTH_TYPE));
        coreType.visibleProperty().set(rollFilters.contains(RollFilter.CORE_TYPE));
        mainValue.visibleProperty().set(rollFilters.contains(RollFilter.MAIN_VALUE));
        findRollButton.disableProperty().set(rollFilters.isEmpty());
    }

    @FXML
    void onFindRoll() {
        viewModel.onFindRoll();
        reCalculateResultHeight();
    }

    @FXML
    void onEditRoll() {
        int selectedIndex = resultTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onEditRoll(new RollProperty(resultTableView.getItems().get(selectedIndex)));
        }
    }

    @FXML
    void onRemoveRoll() {
        int selectedIndex = resultTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onRemoveRoll(resultTableView.getItems().get(selectedIndex).getSku().getValue());
            reCalculateResultHeight();
        }
    }

    //TODO do with bindings
    private void reCalculateResultHeight() {
        resultTableView.prefHeightProperty().set(Double.min(430 - headerPane.getHeight() - filtersPane.getHeight(),
                resultTableView.fixedCellSizeProperty().getValue()
                * (Optional.ofNullable(resultTableView.getItems()).map(List::size).orElse(0))
                + 40.0));
    }

}
