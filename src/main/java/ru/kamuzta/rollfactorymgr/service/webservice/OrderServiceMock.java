package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
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
public class OrderServiceMock implements OrderService {
    private static JsonUtil jsonUtil = JsonUtil.getInstance();
    private final List<Order> remoteOrderRegistry = new ArrayList<>(jsonUtil.getListFromJson("orderRegistry.json", Order.class, CouldNotDeserializeJsonException::new));
    private AtomicLong count = new AtomicLong(remoteOrderRegistry.stream().map(Order::getId).max(Long::compare).orElse(0L));
    private final List<Order> localOrderRegistry = new ArrayList<>();

    @Inject
    RollService rollService;

    @Inject
    ClientService clientService;

    @Override
    public void updateRegistryFromServer() throws WebServiceException {
        try {
            localOrderRegistry.clear();
            localOrderRegistry.addAll(remoteOrderRegistry);
            count = new AtomicLong(localOrderRegistry.stream().map(Order::getId).max(Long::compare).orElse(0L));
        } catch (CouldNotDeserializeJsonException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Order> getLocalRegistry() {
        return localOrderRegistry.stream().map(Order::new).collect(Collectors.toList());
    }

    @Override
    public Order findOrderById(@NotNull Long id) throws WebServiceException {
        return localOrderRegistry.stream().filter(o -> o.getId().equals(id))
                .findFirst()
                .map(Order::new)
                .orElseThrow(() -> new WebServiceException("Order with id " + id + " was not found"));
    }

    @Override
    public List<Order> findOrderByCompanyNamePattern(@NotNull String companyName) throws WebServiceException {
        return localOrderRegistry.stream().filter(o -> o.getClient().getCompanyName().contains(companyName)).map(Order::new).collect(Collectors.toList());
    }

    @Override
    public List<Order> findOrderByParams(@Nullable Long id, @Nullable String companyName, @Nullable OffsetDateTime creationDateFrom, @Nullable OffsetDateTime creationDateTo, @Nullable OrderState state, @Nullable List<OrderLine> lines) throws WebServiceException {
        return localOrderRegistry.stream()
                .filter(o -> String.valueOf(o.getId()).contains((Optional.ofNullable(id).map(String::valueOf)).orElse(String.valueOf(o.getId()))))
                .filter(o -> o.getClient().getCompanyName().contains(Optional.ofNullable(companyName).orElse(o.getClient().getCompanyName())))
                .filter(o -> o.getCreationDate().isAfter(Optional.ofNullable(creationDateFrom).orElse(o.getCreationDate().minusSeconds(1L))))
                .filter(o -> o.getCreationDate().isBefore(Optional.ofNullable(creationDateTo).orElse(o.getCreationDate().plusSeconds(1L))))
                .filter(o -> o.getState() == Optional.ofNullable(state).orElse(o.getState()))
                .filter(o -> o.getLines().equals(Optional.ofNullable(lines).orElse(o.getLines())))
                .map(Order::new).collect(Collectors.toList());
    }

    @Override
    public Order createOrder(@Nullable OffsetDateTime creationDate, @NotNull Client client, @NotNull List<OrderLine> lines) throws WebServiceException {
        try {
            validateCreateOrder(creationDate, client, lines);
            Order newOrder = new Order(count.incrementAndGet(),
                    creationDate != null ? creationDate : OffsetDateTime.now(),
                    client,
                    lines,
                    OrderState.NEW);
            remoteOrderRegistry.add(newOrder);
            updateRegistryFromServer();
            return new Order(newOrder);
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean removeOrderById(@NotNull Long id) throws WebServiceException {
        try {
            validateRemoveOrder(id);
            boolean result = remoteOrderRegistry.remove(findOrderById(id));
            if (result) {
                updateRegistryFromServer();
            }
            return result;
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Order updateOrder(@NotNull Order order) throws WebServiceException {
        try {
            validateUpdateOrder(order);
            remoteOrderRegistry.set(remoteOrderRegistry.indexOf(findOrderById(order.getId())), order);
            updateRegistryFromServer();
            return new Order(order);
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    private void validateCreateOrder(@Nullable OffsetDateTime creationDate, @NotNull Client client, @NotNull List<OrderLine> lines) throws ValidationException {
        if (creationDate != null && creationDate.isAfter(OffsetDateTime.now())) {
            throw new ValidationException("creationDate could not be in future!");
        }

        validateCommonOrderParams(client, lines);

        List<Order> foundDuplicate = findOrderByParams(null, client.getCompanyName(), null, null, null, lines);
        Optional<Order> optionalOrder = foundDuplicate.stream().filter(o -> o.getState() != OrderState.COMPLETED).findFirst();
        if (optionalOrder.isPresent()) {
            throw new ValidationException("Error while trying create duplicate order for companyName " + optionalOrder.get().getClient().getCompanyName());
        }
    }

    private void validateCommonOrderParams(Client client, List<OrderLine> lines) throws ValidationException {
        if (lines.isEmpty()) {
            throw new ValidationException("There is must be at least one line in order!");
        }

        if (lines.stream().mapToInt(OrderLine::getQuantity).anyMatch(q -> q <=0)) {
            throw new ValidationException("There is a line with non-positive quantity!");
        }

        //will throw exception if has unknown roll
        lines.stream().map(OrderLine::getId).forEach(rollService::findRollById);
        //will throw exception if has unknown client
        clientService.findClientById(client.getId());
    }

    private void validateUpdateOrder(Order order) throws ValidationException {
        if (remoteOrderRegistry.stream().noneMatch(c -> c.getId().equals(order.getId()))) {
            throw new ValidationException("Order with id " + order.getId() + " was not found, there is nothing to update");
        }

        validateIfOrderInProgress(order.getId());

        validateCommonOrderParams(order.getClient(), order.getLines());

        List<Order> foundDuplicate = findOrderByParams(null, order.getClient().getCompanyName(), null, null, null, order.getLines());
        Optional<Order> optionalOrder = foundDuplicate.stream().filter(o -> o.getState() != OrderState.COMPLETED).findFirst();
        if (optionalOrder.isPresent()) {
            throw new ValidationException("Error while trying create duplicate order for companyName " + optionalOrder.get().getClient().getCompanyName());
        }
    }

    private void validateRemoveOrder(Long id) throws ValidationException {
        if (remoteOrderRegistry.stream().noneMatch(o -> o.getId().equals(id))) {
            throw new ValidationException("Order with id " + id + " was not found, there is nothing to remove");
        }
        validateIfOrderInProgress(id);
    }

    private void validateIfOrderInProgress(Long id) throws ValidationException {
        if (id == 1L) {
            throw new ValidationException("Order with id " + id + " is in progress at this moment");
        } else {
            log.info("Order with id " + id + " is not in progress");
        }
    }
}
