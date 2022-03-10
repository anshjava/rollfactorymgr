package ru.kamuzta.rollfactorymgr.model.client;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.model.Order;
import ru.kamuzta.rollfactorymgr.model.roll.Roll;

import java.time.ZonedDateTime;

@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@JsonPropertyOrder({"creationDate", "id", "companyName", "city", "address", "buyerName", "phone", "email"})
public class Client implements Comparable<Client>{

    @NotNull
    private ZonedDateTime creationDate;

    @NotNull
    private Long id;

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
        return new Client(this.creationDate, this.id, this.companyName, this.city, this.address, this.buyerName, this.phone, this.email);
    }

    //sort Client by:
    // city (natural)
    // companyName (natural)
    // address (natural)
    // id (natural)
    // creationDate (older first)
    // buyerName (natural)
    // phone (natural)
    // email (natural)
    @Override
    public int compareTo(@NotNull Client that) {
        return ComparisonChain.start()
                .compare(city, that.city)
                .compare(companyName, that.companyName)
                .compare(address, that.address)
                .compare(id, that.id)
                .compare(creationDate, that.creationDate)
                .compare(buyerName, that.buyerName)
                .compare(phone, that.phone)
                .compare(email, that.email)
                .result();
    }
}
