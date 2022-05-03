package ru.kamuzta.rollfactorymgr.ui.client;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientCreateView implements FxmlView<ClientCreateViewModel>, Initializable {

    @FXML
    BorderPane mainPanel;

    @FXML
    Label title;

    @FXML
    private DatePicker clientCreationDate;
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
    private Button createButton;

    @FXML
    private Button cancelButton;

    @InjectViewModel
    private ClientCreateViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText("Setting new client data...");
        //refresh view after All
        setInitialParameters();
    }

    @FXML
    void onCreate(ActionEvent actionEvent) {
        viewModel.createClient();
        viewModel.close(actionEvent);
    }

    @FXML
    void onCancel(ActionEvent actionEvent) {
        viewModel.close(actionEvent);
    }

    private void setInitialParameters() {
        ClientProperty clientProperty = viewModel.getClientProperty();
        clientCreationDate.valueProperty().bindBidirectional(clientProperty.getCreationDate());
        clientCompanyName.textProperty().bindBidirectional(clientProperty.getCompanyName());
        clientCity.textProperty().bindBidirectional(clientProperty.getCity());
        clientAddress.textProperty().bindBidirectional(clientProperty.getAddress());
        clientBuyerName.textProperty().bindBidirectional(clientProperty.getBuyerName());
        clientPhone.textProperty().bindBidirectional(clientProperty.getPhone());
        clientEmail.textProperty().bindBidirectional(clientProperty.getEmail());
    }
}
