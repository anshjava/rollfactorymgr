package ru.kamuzta.rollfactorymgr.ui.roll;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import ru.kamuzta.rollfactorymgr.model.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class RollEditView implements FxmlView<RollEditViewModel>, Initializable {

    @FXML
    BorderPane mainPanel;

    @FXML
    Label title;

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
    TextField rollLength;

    @FXML
    TextField rollDiameter;

    @FXML
    TextField rollWeight;

    @FXML
    private Button applyChangesButton;

    @FXML
    private Button cancelButton;

    @InjectViewModel
    private RollEditViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText("Edit parameters of roll...");
        id.disableProperty().set(true);
        sku.disableProperty().set(true);
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
        rollWeight.disableProperty().set(true);
        //refresh view on model update
        viewModel.getRollProperty().addListener((observable, oldValue, newValue) -> refreshView());
    }

    @FXML
    void onApplyChanges(ActionEvent actionEvent) {
        Roll editedRoll = Roll.builder()
                .id(Long.parseLong(id.getText()))
                .sku(sku.getText())
                .rollType(rollType.getValue())
                .paper(paper.getValue())
                .widthType(widthType.getValue())
                .coreType(coreType.getValue())
                .mainValue(getValueByRollType(rollType.getValue()))
                .build();
        viewModel.editRoll(editedRoll);
        viewModel.close(actionEvent);
    }

    private BigDecimal getValueByRollType(RollType rollType) {
        try {
            switch (rollType) {
                case LENGTH:
                    return new BigDecimal(rollLength.textProperty().get());
                case DIAMETER:
                    return new BigDecimal(rollDiameter.textProperty().get());
                default:
                    throw new IllegalArgumentException(rollType.getTypeName());
            }
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @FXML
    void onCancel(ActionEvent actionEvent) {
        viewModel.close(actionEvent);
    }

    private RollProperty getRollProperty() {
        return viewModel.getRollProperty().get();
    }

    private void refreshView() {
        setInitialParameters();
        configureFields();
        reCalculateAll();
    }

    private void setInitialParameters() {
        RollProperty rollProperty = viewModel.getRollProperty().get();
        id.textProperty().bind(rollProperty.getId().asString());
        sku.textProperty().bind(rollProperty.getSku());
        rollType.valueProperty().bindBidirectional(rollProperty.getRollType());
        paper.valueProperty().bindBidirectional(rollProperty.getPaper());
        widthType.valueProperty().bindBidirectional(rollProperty.getWidthType());
        coreType.valueProperty().bindBidirectional(rollProperty.getCoreType());
        if (rollProperty.getRollType().get().isLength()) {
            rollLength.textProperty().set(rollProperty.getMainValue().getValue().toString());
            rollDiameter.textProperty().set(rollProperty.calculateDiameter().toString());
        } else {
            rollDiameter.textProperty().set(rollProperty.getMainValue().getValue().toString());
            rollLength.textProperty().set(rollProperty.calculateLength().toString());
        }
        rollWeight.textProperty().set(rollProperty.calculateWeight().toString());
    }

    private void configureFields() {
        rollLength.disableProperty().bind(Bindings.createBooleanBinding(() -> rollType.getSelectionModel().getSelectedItem().isDiameter(), rollType.valueProperty()));
        rollDiameter.disableProperty().bind(Bindings.createBooleanBinding(() -> rollType.getSelectionModel().getSelectedItem().isLength(), rollType.valueProperty()));
        //recalculate parameters on change
        rollLength.textProperty().addListener((observable, oldValue, newValue) -> reCalculateAll());
        rollDiameter.textProperty().addListener((observable, oldValue, newValue) -> reCalculateAll());
        rollType.valueProperty().addListener((observable, oldValue, newValue) -> reCalculateAll());
        paper.valueProperty().addListener((observable, oldValue, newValue) -> reCalculateAll());
        widthType.valueProperty().addListener((observable, oldValue, newValue) -> reCalculateAll());
        coreType.valueProperty().addListener((observable, oldValue, newValue) -> reCalculateAll());
    }

    public void reCalculateAll() {
        rollWeight.textProperty().set(viewModel.calculateWeight().toString());
        BigDecimal parsedValue;
        if (rollType.getValue().isLength()) {
            try {
                parsedValue = new BigDecimal(rollLength.textProperty().getValue());
            } catch (NumberFormatException e) {
                parsedValue = BigDecimal.ZERO;
            }
            viewModel.getRollProperty().getValue().getMainValue().set(parsedValue);
            rollDiameter.textProperty().set(viewModel.calculateDiameter().toString());
        } else {
            try {
                parsedValue = new BigDecimal(rollDiameter.textProperty().getValue());
            } catch (NumberFormatException e) {
                parsedValue = BigDecimal.ZERO;
            }
            viewModel.getRollProperty().getValue().getMainValue().set(parsedValue);
            rollLength.textProperty().set(viewModel.calculateLength().toString());
        }
    }
}
