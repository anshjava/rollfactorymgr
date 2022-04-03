package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
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
        try {
            localClientRegistry.clear();
            localClientRegistry.addAll(remoteClientRegistry);
            count = new AtomicLong(localClientRegistry.stream().map(Client::getId).max(Long::compare).orElse(0L));
        } catch (CouldNotDeserializeJsonException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Client> getLocalRegistry() {
        return localClientRegistry.stream().map(Client::new).collect(Collectors.toList());
    }

    @Override
    public Client findClientById(@NotNull Long id) throws WebServiceException {
        return localClientRegistry.stream().filter(r -> r.getId().equals(id))
                .findFirst()
                .map(Client::new)
                .orElseThrow(() -> new WebServiceException("Client with id " + id + " was not found"));
    }

    @Override
    public List<Client> findClientByNamePattern(@NotNull String companyName) throws WebServiceException {
        return localClientRegistry.stream().filter(r -> r.getCompanyName().contains(companyName)).map(Client::new).collect(Collectors.toList());
    }

    @Override
    public List<Client> findClientByParams(@Nullable Long id, @Nullable String companyName, @Nullable OffsetDateTime creationDateFrom, @Nullable OffsetDateTime creationDateTo,
                                           @Nullable String city, @Nullable String address, @Nullable String buyerName, @Nullable String phone, @Nullable String email) throws WebServiceException {
        return localClientRegistry.stream()
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
    public Client createClient(@Nullable OffsetDateTime creationDate, @NotNull String companyName, @NotNull String city, @NotNull String address, @NotNull String buyerName, @NotNull String phone, @NotNull String email) throws WebServiceException {
        try {
            validateCreateClient(creationDate, companyName, city, address, buyerName, phone, email);
            Client newClient = new Client(count.incrementAndGet(),
                    creationDate != null ? creationDate : OffsetDateTime.now(),
                    companyName,
                    city,
                    address,
                    buyerName,
                    phone,
                    email);
            remoteClientRegistry.add(newClient);
            updateRegistryFromServer();
            return new Client(newClient);
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean removeClientById(@NotNull Long id) throws WebServiceException {
        try {
            validateRemoveClient(id);
            boolean result = remoteClientRegistry.remove(findClientById(id));
            if (result) {
                updateRegistryFromServer();
            }
            return result;
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Client updateClient(@NotNull Client client) throws WebServiceException {
        try {
            validateUpdateClient(client);
            remoteClientRegistry.set(remoteClientRegistry.indexOf(findClientById(client.getId())), client);
            updateRegistryFromServer();
            return new Client(client);
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }



    private void validateCreateClient(@Nullable OffsetDateTime creationDate, @NotNull String companyName, @NotNull String city, @NotNull String address, @NotNull String buyerName, @NotNull String phone, @NotNull String email) throws ValidationException {
        if (creationDate != null && creationDate.isAfter(OffsetDateTime.now())) {
            throw new ValidationException("creationDate could not be in future!");
        }
        if (!companyName.matches("[A-Za-z0-9\\-. ]+")) {
            throw new ValidationException("companyName has wrong format! Must contains only A-Z, a-z, 0-9 and .- ");
        }

        if (localClientRegistry.stream().anyMatch(r -> r.getCompanyName().equals(companyName))) {
            throw new ValidationException("Client with companyName " + companyName + " is already exists!");
        }

        validateCommonClientParams(city, address, buyerName, phone, email);

        List<Client> foundDuplicate = findClientByParams(null, null, null, null, city, address, buyerName, phone, email);
        Optional<Client> optionalClient = foundDuplicate.stream().findFirst();
        if (optionalClient.isPresent()) {
            throw new ValidationException("Error while trying create duplicate client of clientName " + optionalClient.get().getCompanyName());
        }
    }

    private void validateCommonClientParams(String city, String address, String buyerName, String phone, String email) throws ValidationException {
        if (!city.matches("[A-Za-z\\- ]+")) {
            throw new ValidationException("City has wrong format! Must contains only A-Z, a-z and - ");
        }
        if (!address.matches("[A-Za-z0-9\\-., ]+")) {
            throw new ValidationException("Address has wrong format! Must contains only A-Z, a-z, 0-9 and -., ");
        }
        if (!buyerName.matches("[A-Za-z\\- ]+")) {
            throw new ValidationException("BuyerName has wrong format! Must contains only A-Z, a-z ");
        }
        if (!phone.matches("7[0-9]{10}")) {
            throw new ValidationException("Phone has wrong format! Must starts with 7 and contains 11 digits total");
        }
        if (!email.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
            throw new ValidationException("Email has wrong format!");
        }
    }

    private void validateUpdateClient(Client client) throws ValidationException {
        if (remoteClientRegistry.stream().noneMatch(c -> c.getId().equals(client.getId()))) {
            throw new ValidationException("Client with id " + client.getId() + " was not found, there is nothing to update");
        }

        validateIfClientInWorkflow(client.getId());

        validateCommonClientParams(client.getCity(), client.getAddress(), client.getBuyerName(), client.getPhone(), client.getEmail());

        List<Client> foundDuplicate = findClientByParams(null, null, null, null, client.getCity(), client.getAddress(), client.getBuyerName(), client.getPhone(), client.getEmail());
        Optional<Client> optionalClient = foundDuplicate.stream().findFirst();
        if (optionalClient.isPresent()) {
            throw new ValidationException("Error while trying create duplicate client of clientName " + optionalClient.get().getCompanyName());
        }
    }

    private void validateRemoveClient(Long id) throws ValidationException {
        if (remoteClientRegistry.stream().noneMatch(r -> r.getId().equals(id))) {
            throw new ValidationException("Client with id " + id + " was not found, there is nothing to remove");
        }
        validateIfClientInWorkflow(id);
    }

    private void validateIfClientInWorkflow(Long id) throws ValidationException {
        if (id == 1L) {
            throw new ValidationException("Client with id " + id + " is in workflow at this moment");
        } else {
            log.info("Client with id " + id + " is not in workflow");
        }
    }
}
