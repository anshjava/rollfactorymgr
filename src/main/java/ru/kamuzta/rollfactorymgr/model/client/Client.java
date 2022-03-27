package ru.kamuzta.rollfactorymgr.model.client;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@JsonPropertyOrder({"id", "creationDate", "companyName", "city", "address", "buyerName", "phone", "email"})
public class Client implements Comparable<Client>{

    @NotNull
    private Long id;

    @NotNull
    private OffsetDateTime creationDate;

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

    @Override
    public Client clone() {
        //all fields are immutable
        return new Client(this.id, this.creationDate, this.companyName, this.city, this.address, this.buyerName, this.phone, this.email);
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
                .result();
    }

    @Override
    public String toString() {
        return String.format("[City: %s, %s] [Client: %d - %s] [%s] [Buyer: %s, %s, %s]",
                city,
                address,
                id,
                companyName,
                creationDate,
                buyerName,
                phone,
                email);
    }
}
