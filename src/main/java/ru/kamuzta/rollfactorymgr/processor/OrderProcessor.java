package ru.kamuzta.rollfactorymgr.processor;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.service.webservice.ClientService;
import ru.kamuzta.rollfactorymgr.service.webservice.OrderService;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OrderProcessor {
    @Inject
    OrderService orderService;

    @Inject
    RollService rollService;

    @Inject
    ClientService clientService;

    public void updateRegistryFromServer() {
        orderService.updateRegistryFromServer();
    }

    public List<Order> getLocalRegistry() {
        return orderService.getLocalRegistry();
    }

    public Order findOrderById(@NotNull Long id) throws WebServiceException {
        return orderService.findOrderById(id);
    }

    public List<Order> findOrderByCompanyNamePattern(@NotNull String companyName) throws WebServiceException {
        return orderService.findOrderByCompanyNamePattern(companyName);
    }

    public List<Order> findOrderByParams(@Nullable Long id, @Nullable String companyName, @Nullable OffsetDateTime creationDateFrom, @Nullable OffsetDateTime creationDateTo, @Nullable OrderState state, @Nullable List<OrderLine> lines) throws WebServiceException {
        return orderService.findOrderByParams(id, companyName, creationDateFrom, creationDateTo, state, lines);
    }

    public Order createOrder(@Nullable OffsetDateTime creationDate, @NotNull Client client, @NotNull List<OrderLine> lines) throws WebServiceException, ValidationException {
        validateCreateOrder(creationDate, client, lines);
        return orderService.createOrder(creationDate, client, lines);
    }

    public boolean removeOrderById(@NotNull Long id) throws WebServiceException, ValidationException {
        validateRemoveOrder(id);
        return orderService.removeOrderById(id);
    }

    public Order updateOrder(@NotNull Order order) throws WebServiceException, ValidationException {
        validateUpdateOrder(order);
        return orderService.updateOrder(order);
    }

    private void validateCreateOrder(@Nullable OffsetDateTime creationDate, @NotNull Client client, @NotNull List<OrderLine> lines) throws ValidationException {
        if (creationDate != null && creationDate.isAfter(OffsetDateTime.now())) {
            throw new ValidationException("creationDate could not be in future!");
        }

        validateCommonOrderParams(client, lines);

        List<Order> foundDuplicate = orderService.findOrderByParams(null, client.getCompanyName(), null, null, null, lines);
        Optional<Order> optionalOrder = foundDuplicate.stream().filter(o -> o.getState() != OrderState.COMPLETED).findFirst();
        if (optionalOrder.isPresent()) {
            throw new ValidationException("Error while trying create duplicate order for companyName " + optionalOrder.get().getClient().getCompanyName());
        }
    }

    private void validateCommonOrderParams(Client client, List<OrderLine> lines) throws ValidationException {
        if (lines.isEmpty()) {
            throw new ValidationException("There is must be at least one line in order!");
        }

        if (lines.stream().mapToInt(OrderLine::getQuantity).anyMatch(q -> q <= 0)) {
            throw new ValidationException("There is a line with non-positive quantity!");
        }

        //will throw exception if has unknown roll
        lines.stream().map(OrderLine::getId).forEach(rollService::findRollById);
        //will throw exception if has unknown client
        clientService.findClientById(client.getId());
    }

    private void validateUpdateOrder(Order order) throws ValidationException {
        if (orderService.getLocalRegistry().stream().noneMatch(c -> c.getId().equals(order.getId()))) {
            throw new ValidationException("Order with id " + order.getId() + " was not found, there is nothing to update");
        }

        validateIfOrderInProgress(order.getId());

        validateCommonOrderParams(order.getClient(), order.getLines());

        List<Order> foundDuplicate = orderService.findOrderByParams(null, order.getClient().getCompanyName(), null, null, null, order.getLines());
        Optional<Order> optionalOrder = foundDuplicate.stream().filter(o -> o.getState() != OrderState.COMPLETED).findFirst();
        if (optionalOrder.isPresent()) {
            throw new ValidationException("Error while trying create duplicate order for companyName " + optionalOrder.get().getClient().getCompanyName());
        }
    }

    private void validateRemoveOrder(Long id) throws ValidationException {
        if (orderService.getLocalRegistry().stream().noneMatch(o -> o.getId().equals(id))) {
            throw new ValidationException("Order with id " + id + " was not found, there is nothing to remove");
        }
        validateIfOrderInProgress(id);
    }

    private void validateIfOrderInProgress(Long id) throws ValidationException {
        if (orderService.findOrderById(id).getState() == OrderState.INPROGRESS) {
            throw new ValidationException("Order with id " + id + " is in progress at this moment");
        } else {
            log.info("Order with id " + id + " is not in progress");
        }
    }
}
