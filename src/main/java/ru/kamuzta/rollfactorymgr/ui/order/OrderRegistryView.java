package ru.kamuzta.rollfactorymgr.ui.order;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.OrderProperty;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.ui.menu.HeaderMenuView;
import ru.kamuzta.rollfactorymgr.ui.table.*;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class OrderRegistryView implements FxmlView<OrderRegistryViewModel>, Initializable {

    @FXML
    public BorderPane headerPane;

    @FXML
    private HeaderMenuView headerMenuView;

    @FXML
    private Button updateOrderRegistryButton;

    @FXML
    private Button editOrderButton;

    @FXML
    private Button cancelOrderButton;

    @FXML
    private TableView<OrderProperty> orderRegistryTableView;
    @FXML
    private TableColumn<OrderProperty, Long> idColumn;
    @FXML
    private TableColumn<OrderProperty, LocalDateTime> creationDateTimeColumn;
    @FXML
    private TableColumn<OrderProperty, Client> clientColumn;
    @FXML
    private TableColumn<OrderProperty, OrderState> stateColumn;
    @FXML
    private TableColumn<OrderProperty, BigDecimal> weightColumn;

    @InjectViewModel
    private OrderRegistryViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //update order registry
        viewModel.onUpdateOrderRegistry();
        //configure table
        orderRegistryTableView.itemsProperty().bind(viewModel.rollPropertiesProperty());
        idColumn.setCellValueFactory(column -> column.getValue().getId());

        creationDateTimeColumn.setCellFactory(column -> new LocalDateTimeCell<>());
        creationDateTimeColumn.setCellValueFactory(column -> column.getValue().getCreationDateTime());

        clientColumn.setCellFactory(column -> new ClientCell<>());
        clientColumn.setCellValueFactory(column -> column.getValue().getClient());

        stateColumn.setCellFactory(column -> new OrderStateCell<>());
        stateColumn.setCellValueFactory(column -> column.getValue().getState());

        weightColumn.setCellValueFactory(column -> column.getValue().calculateWeight());
        weightColumn.setCellFactory(column -> new BigDecimalCell<>(3));

        //configure buttons
        editOrderButton.visibleProperty().bind(orderRegistryTableView.getSelectionModel().selectedIndexProperty().greaterThan(-1));
        editOrderButton.managedProperty().bind(editOrderButton.visibleProperty());
        cancelOrderButton.visibleProperty().bind(editOrderButton.visibleProperty());
        cancelOrderButton.managedProperty().bind(cancelOrderButton.visibleProperty());
    }

    @FXML
    void onUpdateOrderRegistry() {
        viewModel.onUpdateOrderRegistry();
    }

    @FXML
    void onEditOrder() {
        int selectedIndex = orderRegistryTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onEditOrder(new OrderProperty(orderRegistryTableView.getItems().get(selectedIndex)));
        }
    }

    @FXML
    void onCancelOrder() {
        int selectedIndex = orderRegistryTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewModel.onCancelOrder(orderRegistryTableView.getItems().get(selectedIndex).getId().getValue());
        }
    }

}
