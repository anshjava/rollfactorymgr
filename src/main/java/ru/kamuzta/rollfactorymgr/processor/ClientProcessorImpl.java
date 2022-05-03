package ru.kamuzta.rollfactorymgr.processor;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.service.webservice.ClientService;
import ru.kamuzta.rollfactorymgr.service.webservice.OrderService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ClientProcessorImpl implements ClientProcessor {

    @Inject
    ClientService clientService;

    @Inject
    OrderService orderService;

    @Override
    public void updateRegistryFromServer() throws WebServiceException {
        clientService.updateRegistryFromServer();
        orderService.updateRegistryFromServer();
    }

    @Override
    public List<Client> getActiveClientsLocal() {
        return clientService.getActiveClientsLocal();
    }

    @Override
    public Client findClientById(@NotNull Long id) throws WebServiceException {
        return clientService.findClientById(id);
    }

    @Override
    public List<Client> findClientByNamePattern(@NotNull String companyName) throws WebServiceException {
        return clientService.findClientByNamePattern(companyName);
    }

    @Override
    public List<Client> findClientByParams(@Nullable Long id, @Nullable String companyName, @Nullable LocalDate creationDateFrom,
                                           @Nullable LocalDate creationDateTo, @Nullable String city, @Nullable String address,
                                           @Nullable String buyerName, @Nullable String phone, @Nullable String email) throws WebServiceException {
        return clientService.findClientByParams(id, companyName, creationDateFrom, creationDateTo, city, address, buyerName, phone, email);
    }

    @Override
    public Client createClient(@Nullable LocalDate creationDate, @NotNull String companyName, @NotNull String city, @NotNull String address,
                               @NotNull String buyerName, @NotNull String phone, @NotNull String email) throws WebServiceException, ValidationException {
        validateCreateClient(creationDate, companyName, city, address, buyerName, phone, email);
        Client newClient = clientService.createClient(creationDate, companyName, city, address, buyerName, phone, email);
        updateRegistryFromServer();
        return newClient;
    }

    @Override
    public boolean removeClientById(@NotNull Long id) throws WebServiceException, ValidationException {
        validateRemoveClient(id);
        boolean result = clientService.removeClientById(id);
        updateRegistryFromServer();
        return result;
    }

    @Override
    public Client updateClient(@NotNull Client client) throws WebServiceException, ValidationException {
        validateUpdateClient(client);
        Client updatedClient = clientService.updateClient(client);
        updateRegistryFromServer();
        return updatedClient;
    }

    @Override
    public void validateCreateClient(@Nullable LocalDate creationDate, @NotNull String companyName, @NotNull String city, @NotNull String address, @NotNull String buyerName, @NotNull String phone, @NotNull String email) throws ValidationException {
        if (creationDate != null && creationDate.isAfter(LocalDate.now())) {
            throw new ValidationException("creationDate could not be in future!");
        }
        if (!companyName.matches("[A-Za-z0-9\\-. ]+")) {
            throw new ValidationException("companyName has wrong format! Must contains only A-Z, a-z, 0-9 and .- ");
        }

        if (getActiveClientsLocal().stream().anyMatch(r -> r.getCompanyName().equals(companyName))) {
            throw new ValidationException("Client with companyName " + companyName + " is already exists!");
        }

        validateCommonClientParams(city, address, buyerName, phone, email);

        List<Client> foundDuplicate = findClientByParams(null, null, null, null, city, address, buyerName, phone, email);
        Optional<Client> optionalClient = foundDuplicate.stream().findFirst();
        if (optionalClient.isPresent()) {
            throw new ValidationException("Error while trying create duplicate client of clientName " + optionalClient.get().getCompanyName());
        }
    }

    @Override
    public void validateUpdateClient(Client client) throws ValidationException {
        if (getActiveClientsLocal().stream().noneMatch(c -> c.getId().equals(client.getId()))) {
            throw new ValidationException("Client with id " + client.getId() + " was not found, there is nothing to update");
        }

        validateIfClientInWorkflow(client.getId());

        validateCommonClientParams(client.getCity(), client.getAddress(), client.getBuyerName(), client.getPhone(), client.getEmail());

        List<Client> foundDuplicate = findClientByParams(null, null, null, null, client.getCity(),
                client.getAddress(), client.getBuyerName(), client.getPhone(), client.getEmail());
        Optional<Client> optionalClient = foundDuplicate.stream().filter(c -> !c.getId().equals(client.getId())).findFirst();
        if (optionalClient.isPresent()) {
            throw new ValidationException("Error while trying create duplicate client of clientName " + optionalClient.get().getCompanyName());
        }
    }

    @Override
    public void validateRemoveClient(Long id) throws ValidationException {
        if (getActiveClientsLocal().stream().noneMatch(r -> r.getId().equals(id))) {
            throw new ValidationException("Client with id " + id + " was not found, there is nothing to remove");
        }
        validateIfClientInWorkflow(id);
    }

    @Override
    public void validateCommonClientParams(String city, String address, String buyerName, String phone, String email) throws ValidationException {
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

    @Override
    public void validateIfClientInWorkflow(Long id) throws ValidationException {
        Client client = findClientById(id);
        Optional<Order> optionalOrder = orderService.findOrderByParams(null,client.getCompanyName(), null, null, null, null)
                .stream()
                .filter(o -> o.getClient().equals(client))
                .filter(o -> o.getState() != OrderState.COMPLETED && o.getState() != OrderState.CANCELED)
                .findFirst();

        if (optionalOrder.isPresent()) {
            throw new ValidationException("Client with id " + id + " is in workflow at this moment, it has order " + optionalOrder.get().getId() + " in state " + optionalOrder.get().getState());
        } else {
            log.info("Client with id " + id + " is not in workflow");
        }
    }
}
