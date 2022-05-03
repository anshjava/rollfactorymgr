package ru.kamuzta.rollfactorymgr.ui.client;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import ru.kamuzta.rollfactorymgr.model.client.ClientFilter;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.ui.menu.HeaderMenuView;
import ru.kamuzta.rollfactorymgr.ui.table.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientFindView implements FxmlView<ClientFindViewModel>, Initializable {

    @FXML
    public BorderPane headerPane;

    @FXML
    private HeaderMenuView headerMenuView;

    @FXML
    private TitledPane filtersPane;

    @FXML
    private TitledPane resultPane;

    @FXML
    private ListView<ClientFilter> availableFilters;

    @FXML
    private ListView<ClientFilter> selectedFilters;

    @FXML
    private TextField clientId;
    @FXML
    private DatePicker clientCreationDateFrom;
    @FXML
    private DatePicker clientCreationDateTo;
    @FXML
    private TextField clientCompanyName;
    @FXML
    private TextField clientCity;
    @FXML
    private TextField clientAddress;
    @FXML
    private TextField clientBuyerName;
    @FXML
    private TextField clientPhone;
    @FXML
    private TextField clientEmail;

    @FXML
    private Label clientIdLabel;
    @FXML
    private Label clientCreationDateFromLabel;
    @FXML
    private Label clientCreationDateToLabel;
    @FXML
    private Label clientCompanyNameLabel;
    @FXML
    private Label clientCityLabel;
    @FXML
    private Label clientAddressLabel;
    @FXML
    private Label clientBuyerNameLabel;
    @FXML
    private Label clientPhoneLabel;
    @FXML
    private Label clientEmailLabel;

    @FXML
    private Button findClientButton;
    @FXML
    private Button editClientButton;
    @FXML
    private Button removeClientButton;

    @FXML
    private TableView<ClientProperty> resultTableView;
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
    private ClientFindViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureSelectFilterArea();
        configureFilterValues();
        configureInitialFiltersVisibility();
        configureTable();

        //configure buttons
        findClientButton.disableProperty().set(true);

        editClientButton.visibleProperty().bind(resultTableView.getSelectionModel().selectedIndexProperty().greaterThan(-1));
        editClientButton.managedProperty().bind(editClientButton.visibleProperty());

        removeClientButton.visibleProperty().bind(editClientButton.visibleProperty());
        removeClientButton.managedProperty().bind(removeClientButton.visibleProperty());

        //configure detailsBox
        detailsBox.visibleProperty().bind(editClientButton.visibleProperty());
        detailsBox.managedProperty().bind(detailsBox.visibleProperty());
        detailsTitle.setText("Client details:");
    }

    private void configureSelectFilterArea() {
        //configure filters
        availableFilters.itemsProperty().bindBidirectional(viewModel.getAvailableFilters());
        availableFilters.setOnMouseClicked(event -> {
            ClientFilter clickedFilter = availableFilters.getSelectionModel().getSelectedItem();
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
            ClientFilter clickedFilter = selectedFilters.getSelectionModel().getSelectedItem();
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
        resultTableView.itemsProperty().bind(viewModel.clientPropertiesProperty());
        resultTableView.minHeightProperty().bind(resultTableView.prefHeightProperty());
        resultTableView.maxHeightProperty().bind(resultTableView.prefHeightProperty());

        idColumn.setCellValueFactory(column -> column.getValue().getId());

        creationDateColumn.setCellFactory(column -> new LocalDateCell<>());
        creationDateColumn.setCellValueFactory(column -> column.getValue().getCreationDate());

        companyNameColumn.setCellValueFactory(column -> column.getValue().getCompanyName());
        cityColumn.setCellValueFactory(column -> column.getValue().getCity());

        stateColumn.setCellFactory(column -> new ClientStateCell<>());
        stateColumn.setCellValueFactory(column -> column.getValue().getState());
    }


    private void configureFilterValues() {
        ClientProperty clientProperty = viewModel.getClientProperty();
        clientId.textProperty().addListener((observable, oldValue, newValue) -> {
            Long newLongValue;
            try {
                newLongValue = Long.valueOf(newValue);
            } catch (NumberFormatException nfe) {
                newLongValue = null;
            }
            clientProperty.getId().setValue(newLongValue);
        });
        clientCreationDateFrom.valueProperty().set(clientProperty.getCreationDate().getValue());
        clientCreationDateTo.valueProperty().set(clientProperty.getCreationDate().getValue());
        clientCompanyName.textProperty().set(clientProperty.getCompanyName().getValue());
        clientCity.textProperty().set(clientProperty.getCity().getValue());
        clientAddress.textProperty().set(clientProperty.getAddress().getValue());
        clientBuyerName.textProperty().set(clientProperty.getBuyerName().getValue());
        clientPhone.textProperty().set(clientProperty.getPhone().getValue());
        clientEmail.textProperty().set(clientProperty.getEmail().getValue());
    }

    private void configureInitialFiltersVisibility() {
        clientId.visibleProperty().set(false);
        clientId.managedProperty().bind(clientId.visibleProperty());
        clientIdLabel.visibleProperty().bind(clientId.visibleProperty());
        clientIdLabel.managedProperty().bind(clientIdLabel.visibleProperty());

        clientCreationDateFrom.visibleProperty().set(false);
        clientCreationDateFrom.managedProperty().bind(clientCreationDateFrom.visibleProperty());
        clientCreationDateFromLabel.visibleProperty().bind(clientCreationDateFrom.visibleProperty());
        clientCreationDateFromLabel.managedProperty().bind(clientCreationDateFromLabel.visibleProperty());

        clientCreationDateTo.visibleProperty().bind(clientCreationDateFrom.visibleProperty());
        clientCreationDateTo.managedProperty().bind(clientCreationDateTo.visibleProperty());
        clientCreationDateToLabel.visibleProperty().bind(clientCreationDateTo.visibleProperty());
        clientCreationDateToLabel.managedProperty().bind(clientCreationDateToLabel.visibleProperty());

        clientCompanyName.visibleProperty().set(false);
        clientCompanyName.managedProperty().bind(clientCompanyName.visibleProperty());
        clientCompanyNameLabel.visibleProperty().bind(clientCompanyName.visibleProperty());
        clientCompanyNameLabel.managedProperty().bind(clientCompanyNameLabel.visibleProperty());

        clientCity.visibleProperty().set(false);
        clientCity.managedProperty().bind(clientCity.visibleProperty());
        clientCityLabel.visibleProperty().bind(clientCity.visibleProperty());
        clientCityLabel.managedProperty().bind(clientCityLabel.visibleProperty());

        clientAddress.visibleProperty().set(false);
        clientAddress.managedProperty().bind(clientAddress.visibleProperty());
        clientAddressLabel.visibleProperty().bind(clientAddress.visibleProperty());
        clientAddressLabel.managedProperty().bind(clientAddressLabel.visibleProperty());

        clientBuyerName.visibleProperty().set(false);
        clientBuyerName.managedProperty().bind(clientBuyerName.visibleProperty());
        clientBuyerNameLabel.visibleProperty().bind(clientBuyerName.visibleProperty());
        clientBuyerNameLabel.managedProperty().bind(clientBuyerNameLabel.visibleProperty());

        clientPhone.visibleProperty().set(false);
        clientPhone.managedProperty().bind(clientPhone.visibleProperty());
        clientPhoneLabel.visibleProperty().bind(clientPhone.visibleProperty());
        clientPhoneLabel.managedProperty().bind(clientPhoneLabel.visibleProperty());

        clientEmail.visibleProperty().set(false);
        clientEmail.managedProperty().bind(clientEmail.visibleProperty());
        clientEmailLabel.visibleProperty().bind(clientEmail.visibleProperty());
        clientEmailLabel.managedProperty().bind(clientEmailLabel.visibleProperty());
    }

    private void refreshFilterVisibility(ObservableList<ClientFilter> clientFilters) {
        clientId.visibleProperty().set(clientFilters.contains(ClientFilter.ID));
        clientCreationDateFrom.visibleProperty().set(clientFilters.contains(ClientFilter.CREATION_DATE));
        clientCompanyName.visibleProperty().set(clientFilters.contains(ClientFilter.COMPANY_NAME));
        clientCity.visibleProperty().set(clientFilters.contains(ClientFilter.CITY));
        clientAddress.visibleProperty().set(clientFilters.contains(ClientFilter.ADDRESS));
        clientBuyerName.visibleProperty().set(clientFilters.contains(ClientFilter.BUYER_NAME));
        clientPhone.visibleProperty().set(clientFilters.contains(ClientFilter.PHONE));
        clientEmail.visibleProperty().set(clientFilters.contains(ClientFilter.EMAIL));
        findClientButton.disableProperty().set(clientFilters.isEmpty());
    }

    @FXML
    void onFindClient() {
        viewModel.onFindClient();
        reCalculateResultHeight();
    }

    @FXML
    void onEditClient() {
        int selectedIndex = resultTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onEditClient(new ClientProperty(resultTableView.getItems().get(selectedIndex)));
        }
    }

    @FXML
    void onRemoveClient() {
        int selectedIndex = resultTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onRemoveClient(resultTableView.getItems().get(selectedIndex).getId().getValue());
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
