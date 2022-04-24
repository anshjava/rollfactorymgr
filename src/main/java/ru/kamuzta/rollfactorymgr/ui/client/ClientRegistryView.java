package ru.kamuzta.rollfactorymgr.ui.client;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.model.roll.CoreType;
import ru.kamuzta.rollfactorymgr.ui.menu.HeaderMenuView;
import ru.kamuzta.rollfactorymgr.ui.table.*;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ClientRegistryView implements FxmlView<ClientRegistryViewModel>, Initializable {

    @FXML
    public BorderPane headerPane;

    @FXML
    private HeaderMenuView headerMenuView;

    @FXML
    private Button updateClientRegistryButton;

    @FXML
    private Button editClientButton;

    @FXML
    private Button removeClientButton;

    @FXML
    private TableView<ClientProperty> clientRegistryTableView;
    @FXML
    private TableColumn<ClientProperty, Long> idColumn;
    @FXML
    private TableColumn<ClientProperty, LocalDate> creationDateColumn;
    @FXML
    private TableColumn<ClientProperty, String> companyNameColumn;
    @FXML
    private TableColumn<ClientProperty, String> cityColumn;
    @FXML
    private TableColumn<ClientProperty, ClientState> stateColumn;

    @FXML
    private VBox detailsBox;
    @FXML
    private Label detailsTitle;
    @FXML
    private TextField detailsId;
    @FXML
    private DatePicker detailsCreationDate;
    @FXML
    private TextField detailsCompanyName;
    @FXML
    private TextField detailsCity;
    @FXML
    private TextField detailsAddress;
    @FXML
    private TextField detailsBuyerName;
    @FXML
    private TextField detailsPhone;
    @FXML
    private TextField detailsEmail;
    @FXML
    private ComboBox<ClientState> detailsState;


    @InjectViewModel
    private ClientRegistryViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //update roll registry
        viewModel.onUpdateClientRegistry();
        //configure table
        clientRegistryTableView.itemsProperty().bind(viewModel.clientPropertiesProperty());

        idColumn.setCellValueFactory(column -> column.getValue().getId());

        creationDateColumn.setCellFactory(column -> new LocalDateCell<>());
        creationDateColumn.setCellValueFactory(column -> column.getValue().getCreationDate());

        companyNameColumn.setCellValueFactory(column -> column.getValue().getCompanyName());
        cityColumn.setCellValueFactory(column -> column.getValue().getCity());

        stateColumn.setCellFactory(column -> new ClientStateCell<>());
        stateColumn.setCellValueFactory(column -> column.getValue().getState());

        //configure buttons
        editClientButton.visibleProperty().bind(clientRegistryTableView.getSelectionModel().selectedIndexProperty().greaterThan(-1));
        editClientButton.managedProperty().bind(editClientButton.visibleProperty());
        removeClientButton.visibleProperty().bind(editClientButton.visibleProperty());
        removeClientButton.managedProperty().bind(removeClientButton.visibleProperty());

        //configure detailsBox
        detailsBox.visibleProperty().bind(editClientButton.visibleProperty());
        detailsBox.managedProperty().bind(detailsBox.visibleProperty());
        detailsTitle.setText("Client details:");

        detailsState.setItems(FXCollections.observableArrayList(ClientState.values()));
        detailsState.setConverter(new StringConverter<ClientState>() {
            @Override
            public String toString(ClientState clientState) {
                return clientState.toString();
            }

            @Override
            public ClientState fromString(String clientState) {
                return ClientState.valueOf(clientState);
            }
        });

        clientRegistryTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int selectedIndex = clientRegistryTableView.getSelectionModel().getSelectedIndex();
                    if (selectedIndex >= 0) {
                        ClientProperty clientProperty = clientRegistryTableView.getItems().get(selectedIndex);
                        detailsId.textProperty().set(clientProperty.getId().getValue().toString());
                        detailsCreationDate.setValue(clientProperty.getCreationDate().getValue());
                        detailsCompanyName.textProperty().set(clientProperty.getCompanyName().getValue());
                        detailsCity.textProperty().set(clientProperty.getCity().getValue());
                        detailsAddress.textProperty().set(clientProperty.getAddress().getValue());
                        detailsBuyerName.textProperty().set(clientProperty.getBuyerName().getValue());
                        detailsEmail.textProperty().set(clientProperty.getEmail().getValue());
                        detailsPhone.textProperty().set(clientProperty.getPhone().getValue());
                        detailsState.valueProperty().set(clientProperty.getState().getValue());
                    }
                }
        );

    }

    @FXML
    void onUpdateClientRegistry() {
        viewModel.onUpdateClientRegistry();
    }

    @FXML
    void onEditClient() {
        int selectedIndex = clientRegistryTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onEditClient(new ClientProperty(clientRegistryTableView.getItems().get(selectedIndex)));
        }
    }

    @FXML
    void onRemoveClient() {
        int selectedIndex = clientRegistryTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onRemoveClient(clientRegistryTableView.getItems().get(selectedIndex).getId().getValue());
        }
    }

}
