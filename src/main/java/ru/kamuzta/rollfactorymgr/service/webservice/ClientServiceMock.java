package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class ClientServiceMock implements ClientService {
    private static JsonUtil jsonUtil = JsonUtil.getInstance();
    private final List<Client> remoteClientRegistry = new ArrayList<>(jsonUtil.getListFromJson("clientRegistry.json", Client.class, CouldNotDeserializeJsonException::new));
    private AtomicLong count = new AtomicLong(remoteClientRegistry.stream().map(Client::getId).max(Long::compare).orElse(0L));
    private final List<Client> localClientRegistry = new ArrayList<>();

    @Override
    public void updateRegistryFromServer() throws WebServiceException {
        localClientRegistry.clear();
        localClientRegistry.addAll(remoteClientRegistry);
        count = new AtomicLong(remoteClientRegistry.stream().map(Client::getId).max(Long::compare).orElse(0L));
    }

    @Override
    public List<Client> getActiveClientsLocal() {
        return localClientRegistry.stream()
                .filter(client -> client.getState() == ClientState.ACTIVE)
                .map(Client::new)
                .collect(Collectors.toList());
    }

    @Override
    public Client findClientById(@NotNull Long id) throws WebServiceException {
        return localClientRegistry.stream()
                .filter(client -> client.getState() == ClientState.ACTIVE)
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(Client::new)
                .orElseThrow(() -> new WebServiceException("Client with id " + id + " was not found"));
    }

    @Override
    public List<Client> findClientByNamePattern(@NotNull String companyName) throws WebServiceException {
        return localClientRegistry.stream()
                .filter(client -> client.getState() == ClientState.ACTIVE)
                .filter(r -> r.getCompanyName().contains(companyName))
                .map(Client::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> findClientByParams(@Nullable Long id, @Nullable String companyName, @Nullable OffsetDateTime creationDateFrom, @Nullable OffsetDateTime creationDateTo,
                                           @Nullable String city, @Nullable String address, @Nullable String buyerName, @Nullable String phone, @Nullable String email) throws WebServiceException {
        return localClientRegistry.stream()
                .filter(c -> c.getState() == ClientState.ACTIVE)
                .filter(c -> String.valueOf(c.getId()).contains((Optional.ofNullable(id).map(String::valueOf)).orElse(String.valueOf(c.getId()))))
                .filter(c -> c.getCompanyName().contains(Optional.ofNullable(companyName).orElse(c.getCompanyName())))
                .filter(c -> c.getCreationDate().isAfter(Optional.ofNullable(creationDateFrom).orElse(c.getCreationDate().minusSeconds(1L))))
                .filter(c -> c.getCreationDate().isBefore(Optional.ofNullable(creationDateTo).orElse(c.getCreationDate().plusSeconds(1L))))
                .filter(c -> c.getCity().contains(Optional.ofNullable(city).orElse(c.getCity())))
                .filter(c -> c.getAddress().contains(Optional.ofNullable(address).orElse(c.getAddress())))
                .filter(c -> c.getBuyerName().contains(Optional.ofNullable(buyerName).orElse(c.getBuyerName())))
                .filter(c -> c.getPhone().contains(Optional.ofNullable(phone).orElse(c.getPhone())))
                .filter(c -> c.getEmail().contains(Optional.ofNullable(email).orElse(c.getEmail())))
                .map(Client::new).collect(Collectors.toList());
    }

    @Override
    public Client createClient(@Nullable OffsetDateTime creationDate, @NotNull String companyName, @NotNull String city,
                               @NotNull String address, @NotNull String buyerName, @NotNull String phone, @NotNull String email) throws WebServiceException {
        Client newClient = new Client(count.incrementAndGet(),
                creationDate != null ? creationDate : OffsetDateTime.now(),
                companyName,
                city,
                address,
                buyerName,
                phone,
                email,
                ClientState.ACTIVE);
        remoteClientRegistry.add(newClient);
        return new Client(newClient);
    }

    @Override
    public boolean removeClientById(@NotNull Long id) throws WebServiceException {
        Client clientToDelete = findClientById(id);
        clientToDelete.setState(ClientState.DELETED);
        Client oldClient = findClientById(clientToDelete.getId());
        remoteClientRegistry.set(remoteClientRegistry.indexOf(oldClient), clientToDelete);
        return true;
    }

    @Override
    public Client updateClient(@NotNull Client client) throws WebServiceException {
        Client oldClient = findClientById(client.getId());
        remoteClientRegistry.set(remoteClientRegistry.indexOf(oldClient), client);
        return new Client(client);
    }











}
