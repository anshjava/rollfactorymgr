package ru.kamuzta.rollfactorymgr.ui.client;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.ui.menu.HeaderMenuView;
import ru.kamuzta.rollfactorymgr.ui.table.*;

import java.net.URL;
import java.time.OffsetDateTime;
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
    private TableColumn<ClientProperty, OffsetDateTime> creationDateColumn;
    @FXML
    private TableColumn<ClientProperty, String> companyNameColumn;
    @FXML
    private TableColumn<ClientProperty, String> cityColumn;
    @FXML
    private TableColumn<ClientProperty, String> addressColumn;
    @FXML
    private TableColumn<ClientProperty, String> buyerNameColumn;
    @FXML
    private TableColumn<ClientProperty, String> phoneColumn;
    @FXML
    private TableColumn<ClientProperty, String> emailColumn;
    @FXML
    private TableColumn<ClientProperty, ClientState> stateColumn;

    @InjectViewModel
    private ClientRegistryViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //update roll registry
        viewModel.onUpdateClientRegistry();
        //configure table
        clientRegistryTableView.itemsProperty().bind(viewModel.clientPropertiesProperty());

        idColumn.setCellValueFactory(column -> column.getValue().getId());

        creationDateColumn.setCellFactory(column -> new OffsetDateTimeCell<>());
        creationDateColumn.setCellValueFactory(column -> column.getValue().getCreationDate());

        companyNameColumn.setCellValueFactory(column -> column.getValue().getCompanyName());
        cityColumn.setCellValueFactory(column -> column.getValue().getCity());
        addressColumn.setCellValueFactory(column -> column.getValue().getAddress());
        buyerNameColumn.setCellValueFactory(column -> column.getValue().getBuyerName());
        phoneColumn.setCellValueFactory(column -> column.getValue().getPhone());
        emailColumn.setCellValueFactory(column -> column.getValue().getEmail());

        stateColumn.setCellFactory(column -> new ClientStateCell<>());
        stateColumn.setCellValueFactory(column -> column.getValue().getState());

        //configure buttons
        editClientButton.visibleProperty().bind(clientRegistryTableView.getSelectionModel().selectedIndexProperty().greaterThan(-1));
        editClientButton.managedProperty().bind(editClientButton.visibleProperty());
        removeClientButton.visibleProperty().bind(editClientButton.visibleProperty());
        removeClientButton.managedProperty().bind(removeClientButton.visibleProperty());
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
