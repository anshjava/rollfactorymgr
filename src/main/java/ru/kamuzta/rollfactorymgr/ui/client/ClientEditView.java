package ru.kamuzta.rollfactorymgr.ui.client;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import ru.kamuzta.rollfactorymgr.model.client.ClientProperty;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientEditView implements FxmlView<ClientEditViewModel>, Initializable {

    @FXML
    BorderPane mainPanel;

    @FXML
    Label title;

    @FXML
    private TextField clientId;
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
    private ComboBox<ClientState> clientState;

    @FXML
    private Button applyChangesButton;

    @FXML
    private Button cancelButton;

    @InjectViewModel
    private ClientEditViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText("Edit client data...");
        clientId.disableProperty().set(true);
        clientState.disableProperty().set(true);
        clientState.setItems(FXCollections.observableArrayList(ClientState.values()));
        clientState.setConverter(new StringConverter<ClientState>() {
            @Override
            public String toString(ClientState clientState) {
                return clientState.toString();
            }

            @Override
            public ClientState fromString(String clientState) {
                return ClientState.valueOf(clientState);
            }
        });

        //refresh view on model update
        viewModel.getClientProperty().addListener((observable, oldValue, newValue) -> refreshView());

    }

    @FXML
    void onApplyChanges(ActionEvent actionEvent) {
        viewModel.editClient();
        viewModel.close(actionEvent);
    }

    @FXML
    void onCancel(ActionEvent actionEvent) {
        viewModel.close(actionEvent);
    }


    private void refreshView() {
        setInitialParameters();
    }

    private void setInitialParameters() {
        ClientProperty clientProperty = viewModel.getClientProperty().get();
        clientId.textProperty().set(clientProperty.getId().getValue().toString());
        clientCreationDate.valueProperty().bindBidirectional(clientProperty.getCreationDate());
        clientCompanyName.textProperty().bindBidirectional(clientProperty.getCompanyName());
        clientCity.textProperty().bindBidirectional(clientProperty.getCity());
        clientAddress.textProperty().bindBidirectional(clientProperty.getAddress());
        clientBuyerName.textProperty().bindBidirectional(clientProperty.getBuyerName());
        clientPhone.textProperty().bindBidirectional(clientProperty.getPhone());
        clientEmail.textProperty().bindBidirectional(clientProperty.getEmail());
        clientState.valueProperty().bindBidirectional(clientProperty.getState());
    }
}
