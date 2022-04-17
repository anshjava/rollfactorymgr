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
import ru.kamuzta.rollfactorymgr.model.roll.Roll;
import ru.kamuzta.rollfactorymgr.service.webservice.ClientService;
import ru.kamuzta.rollfactorymgr.service.webservice.OrderService;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OrderProcessorImpl implements OrderProcessor {

    @Inject
    OrderService orderService;

    @Inject
    RollService rollService;

    @Inject
    ClientService clientService;

    public void updateRegistryFromServer() {
        orderService.updateRegistryFromServer();
        rollService.updateRegistryFromServer();
        clientService.updateRegistryFromServer();
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

    public List<Order> findOrderByParams(@Nullable Long id, @Nullable String companyName, @Nullable OffsetDateTime creationDateFrom, @Nullable OffsetDateTime creationDateTo, @Nullable OrderState state, @Nullable String rollSku) throws WebServiceException {
        return orderService.findOrderByParams(id, companyName, creationDateFrom, creationDateTo, state, rollSku);
    }

    public Order createOrder(@Nullable OffsetDateTime creationDate, @NotNull Client client, @NotNull List<OrderLine> lines) throws WebServiceException, ValidationException {
        validateCreateOrder(creationDate, client, lines);
        Order newOrder = orderService.createOrder(creationDate, client, lines);
        updateRegistryFromServer();
        return newOrder;
    }

    public boolean removeOrderById(@NotNull Long id) throws WebServiceException, ValidationException {
        validateRemoveOrder(id);
        boolean result = orderService.removeOrderById(id);
        updateRegistryFromServer();
        return result;
    }

    public Order updateOrder(@NotNull Order order) throws WebServiceException, ValidationException {
        validateUpdateOrder(order);
        Order updatedOrder = orderService.updateOrder(order);
        updateRegistryFromServer();
        return updatedOrder;
    }

    @Override
    public void validateCreateOrder(@Nullable OffsetDateTime creationDate, @NotNull Client client, @NotNull List<OrderLine> lines) throws ValidationException {
        if (creationDate != null && creationDate.isAfter(OffsetDateTime.now())) {
            throw new ValidationException("creationDate could not be in future!");
        }

        validateCommonOrderParams(client, lines);

        List<Order> foundDuplicate = orderService.findOrderByCompanyNamePattern(client.getCompanyName());
        Optional<Order> optionalOrder = foundDuplicate.stream()
                .filter(o -> o.getState() != OrderState.COMPLETED)
                .filter(o -> o.getClient().getCompanyName().equals(client.getCompanyName()))
                .filter(o -> o.getLines().equals(lines))
                .findFirst();
        if (optionalOrder.isPresent()) {
            throw new ValidationException("Error while trying create duplicate order for companyName " + optionalOrder.get().getClient().getCompanyName());
        }
    }

    @Override
    public void validateUpdateOrder(Order order) throws ValidationException {
        if (getLocalRegistry().stream().noneMatch(c -> c.getId().equals(order.getId()))) {
            throw new ValidationException("Order with id " + order.getId() + " was not found, there is nothing to update");
        }

        validateIfOrderInProgress(order.getId());

        validateCommonOrderParams(order.getClient(), order.getLines());

        List<Order> foundDuplicate = orderService.findOrderByCompanyNamePattern(order.getClient().getCompanyName());
        Optional<Order> optionalOrder = foundDuplicate.stream()
                .filter(o -> o.getState() != OrderState.COMPLETED)
                .filter(o -> o.getClient().getCompanyName().equals(order.getClient().getCompanyName()))
                .filter(o -> o.getLines().equals(order.getLines()))
                .findFirst();
        if (optionalOrder.isPresent()) {
            throw new ValidationException("Error while trying create duplicate order for companyName " + optionalOrder.get().getClient().getCompanyName());
        }
    }

    @Override
    public void validateRemoveOrder(Long id) throws ValidationException {
        if (getLocalRegistry().stream().noneMatch(o -> o.getId().equals(id))) {
            throw new ValidationException("Order with id " + id + " was not found, there is nothing to remove");
        }
        validateIfOrderInProgress(id);
    }

    @Override
    public void validateCommonOrderParams(Client client, List<OrderLine> lines) throws ValidationException {
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

    @Override
    public void validateIfOrderInProgress(Long id) throws ValidationException {
        if (orderService.findOrderById(id).getState() == OrderState.INPROGRESS) {
            throw new ValidationException("Order with id " + id + " is in progress at this moment");
        } else {
            log.info("Order with id " + id + " is not in progress");
        }
    }
}
