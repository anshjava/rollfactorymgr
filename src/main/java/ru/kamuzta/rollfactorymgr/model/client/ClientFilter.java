package ru.kamuzta.rollfactorymgr.model.client;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ClientFilter {
    ID("id"),
    CREATION_DATE("creationDate"),
    COMPANY_NAME("companyName"),
    CITY("city"),
    ADDRESS("address"),
    BUYER_NAME("buyerName"),
    PHONE("phone"),
    EMAIL("email");

    private String filterName;

    @Override
    public String toString() {
        return name();
    }
}
