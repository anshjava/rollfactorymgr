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

import java.time.LocalDateTime;
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

    public List<Order> findOrderByParams(@Nullable Long id, @Nullable String companyName, @Nullable LocalDateTime creationDateTimeFrom, @Nullable LocalDateTime creationDateTimeTo, @Nullable OrderState state, @Nullable String rollSku) throws WebServiceException {
        return orderService.findOrderByParams(id, companyName, creationDateTimeFrom, creationDateTimeTo, state, rollSku);
    }

    public Order createOrder(@Nullable LocalDateTime creationDateTime, @NotNull Client client, @NotNull List<OrderLine> lines) throws WebServiceException, ValidationException {
        validateCreateOrder(creationDateTime, client, lines);
        Order newOrder = orderService.createOrder(creationDateTime, client, lines);
        updateRegistryFromServer();
        return newOrder;
    }

    public boolean cancelOrderById(@NotNull Long id) throws WebServiceException, ValidationException {
        validateCancelOrder(id);
        boolean result = orderService.cancelOrderById(id);
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
    public void validateCreateOrder(@Nullable LocalDateTime creationDateTime, @NotNull Client client, @NotNull List<OrderLine> lines) throws ValidationException {
        if (creationDateTime != null && creationDateTime.isAfter(LocalDateTime.now())) {
            throw new ValidationException("creationDateTime could not be in future!");
        }

        validateCommonOrderParams(client, lines);

        List<Order> foundDuplicate = orderService.findOrderByCompanyNamePattern(client.getCompanyName());
        Optional<Order> optionalOrder = foundDuplicate.stream()
                .filter(o -> o.getState() != OrderState.COMPLETED && o.getState() != OrderState.CANCELED)
                .filter(o -> o.getClient().equals(client))
                .filter(o -> o.getLines().equals(lines))
                .findFirst();
        if (optionalOrder.isPresent()) {
            throw new ValidationException("Error while trying create duplicate order for client " + optionalOrder.get().getClient().getCompanyName() + " order id " + optionalOrder.get().getId());
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
                .filter(o -> o.getState() != OrderState.COMPLETED && o.getState() != OrderState.CANCELED)
                .filter(o -> o.getClient().equals(order.getClient()))
                .filter(o -> o.getLines().equals(order.getLines()))
                .filter(o -> o.getCreationDateTime().equals(order.getCreationDateTime()))
                .findFirst();
        if (optionalOrder.isPresent()) {
            throw new ValidationException("Error while trying create duplicate order for client " + optionalOrder.get().getClient().getCompanyName() + " order id " + optionalOrder.get().getId());
        }
    }

    @Override
    public void validateCancelOrder(Long id) throws ValidationException {
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

        //will throw exception if has unknown roll or removed roll
        try {
            lines.stream().map(OrderLine::getRoll).map(Roll::getId).forEach(rollService::findRollById);
        } catch (WebServiceException e) {
            throw new ValidationException("There is unkwnown or removed roll in order lines!", e);
        }
        //will throw exception if has unknown client
        try {
            clientService.findClientById(client.getId());
        } catch (WebServiceException e) {
            throw new ValidationException("There is unkwnown or removed client in order!", e);
        }
    }

    @Override
    public void validateIfOrderInProgress(Long id) throws ValidationException {
        OrderState state = orderService.findOrderById(id).getState();
        switch (state) {
            case NEW:
            case QUEUED:
                log.info("Order with id " + id + " is not in progress");
                break;
            case INPROGRESS:
                throw new ValidationException("Order with id " + id + " is in progress at this moment");
            case COMPLETED:
                throw new ValidationException("Order with id " + id + " is completed at this moment");
            case CANCELED:
                throw new ValidationException("Order with id " + id + " is canceled at this moment");
            default:
                throw new IllegalArgumentException("state is illegal " + state);
        }
    }
}
