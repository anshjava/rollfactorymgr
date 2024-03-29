package ru.kamuzta.rollfactorymgr.model.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.google.common.collect.ComparisonChain;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonPropertyOrder({"id", "creationDate", "companyName", "city", "address", "buyerName", "phone", "email", "state"})
public class Client implements Comparable<Client>{

    @NotNull
    private Long id;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate creationDate;
    @NotNull
    private String companyName;
    @NotNull
    private String city;
    @NotNull
    private String address;
    @NotNull
    private String buyerName;
    @NotNull
    private String phone;
    @NotNull
    private String email;
    @NotNull
    private ClientState state;

    //copy constructor
    public Client(Client that) {
        this.id = that.id;
        this.creationDate = that.creationDate;
        this.companyName = that.companyName;
        this.city = that.city;
        this.address = that.address;
        this.buyerName = that.buyerName;
        this.phone = that.phone;
        this.email = that.email;
        this.state = that.state;
    }

    //sort Client by:
    // city (natural)
    // address (natural)
    // companyName (natural)
    // id (natural)
    // creationDate (older first)
    // buyerName (natural)
    // phone (natural)
    // email (natural)
    // state (ACTIVE first)
    @Override
    public int compareTo(@NotNull Client that) {
        return ComparisonChain.start()
                .compare(city, that.city)
                .compare(address, that.address)
                .compare(companyName, that.companyName)
                .compare(id, that.id)
                .compare(creationDate, that.creationDate)
                .compare(buyerName, that.buyerName)
                .compare(phone, that.phone)
                .compare(email, that.email)
                .compare(state, that.state)
                .result();
    }

    @Override
    public String toString() {
        return String.format("[%s] [City: %s, %s] [Client: %d - %s] [%s] [Buyer: %s, %s, %s]",
                state,
                city,
                address,
                id,
                companyName,
                creationDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                buyerName,
                phone,
                email);
    }
}
