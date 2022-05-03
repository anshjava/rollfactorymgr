package ru.kamuzta.rollfactorymgr.ui.client;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.ui.menu.HeaderMenuView;
import ru.kamuzta.rollfactorymgr.ui.table.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private Label detailsId;
    @FXML
    private Label detailsCreationDate;
    @FXML
    private Label detailsCompanyName;
    @FXML
    private Label detailsCity;
    @FXML
    private Label detailsAddress;
    @FXML
    private Label detailsBuyerName;
    @FXML
    private Label detailsPhone;
    @FXML
    private Label detailsEmail;
    @FXML
    private Label detailsState;


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

        clientRegistryTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    int selectedIndex = clientRegistryTableView.getSelectionModel().getSelectedIndex();
                    if (selectedIndex >= 0) {
                        ClientProperty clientProperty = clientRegistryTableView.getItems().get(selectedIndex);
                        detailsId.textProperty().set(clientProperty.getId().getValue().toString());
                        detailsCreationDate.textProperty().set(clientProperty.getCreationDate().getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
                        detailsCompanyName.textProperty().set(clientProperty.getCompanyName().getValue());
                        detailsCity.textProperty().set(clientProperty.getCity().getValue());
                        detailsAddress.textProperty().set(clientProperty.getAddress().getValue());
                        detailsBuyerName.textProperty().set(clientProperty.getBuyerName().getValue());
                        detailsEmail.textProperty().set(clientProperty.getEmail().getValue());
                        detailsPhone.textProperty().set(clientProperty.getPhone().getValue());
                        detailsState.textProperty().set(clientProperty.getState().getValue().toString());
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
