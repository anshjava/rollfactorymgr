package ru.kamuzta.rollfactorymgr.model.client;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

/**
 * Client wrapper for FX
 */
@Getter
@AllArgsConstructor
public class ClientProperty {
    private final ObjectProperty<Long> id;
    private final ObjectProperty<OffsetDateTime> creationDate;
    private final StringProperty companyName;
    private final StringProperty city;
    private final StringProperty address;
    private final StringProperty buyerName;
    private final StringProperty phone;
    private final StringProperty email;
    private final ObjectProperty<ClientState> state;

    //copy constructor
    public ClientProperty(ClientProperty that) {
        this.id = new SimpleObjectProperty<>(that.id.getValue());
        this.creationDate = new SimpleObjectProperty<>(that.creationDate.getValue());
        this.companyName = new SimpleStringProperty(that.companyName.getValue());
        this.city = new SimpleStringProperty(that.city.getValue());
        this.address = new SimpleStringProperty(that.address.getValue());
        this.buyerName = new SimpleStringProperty(that.buyerName.getValue());
        this.phone = new SimpleStringProperty(that.phone.getValue());
        this.email = new SimpleStringProperty(that.email.getValue());
        this.state = new SimpleObjectProperty<>(that.state.getValue());
    }

    public static ClientProperty getSample() {
        return new ClientProperty(
                new SimpleObjectProperty<>(null),
                new SimpleObjectProperty<>(OffsetDateTime.now()),
                new SimpleStringProperty(""),
                new SimpleStringProperty(""),
                new SimpleStringProperty(""),
                new SimpleStringProperty(""),
                new SimpleStringProperty(""),
                new SimpleStringProperty(""),
                new SimpleObjectProperty<>(ClientState.ACTIVE)
        );
    }

}
