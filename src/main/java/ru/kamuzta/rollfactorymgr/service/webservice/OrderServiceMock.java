package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.time.LocalDateTime;
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

    @Override
    public void updateRegistryFromServer() throws WebServiceException {
        localOrderRegistry.clear();
        localOrderRegistry.addAll(remoteOrderRegistry);
        count = new AtomicLong(localOrderRegistry.stream().map(Order::getId).max(Long::compare).orElse(0L));
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
    public List<Order> findOrderByParams(@Nullable Long id, @Nullable String companyName, @Nullable LocalDateTime creationDateTimeFrom, @Nullable LocalDateTime creationDateTimeTo, @Nullable OrderState state, @Nullable String rollSku) throws WebServiceException {
        return localOrderRegistry.stream()
                .filter(o -> String.valueOf(o.getId()).contains((Optional.ofNullable(id).map(String::valueOf)).orElse(String.valueOf(o.getId()))))
                .filter(o -> o.getClient().getCompanyName().contains(Optional.ofNullable(companyName).orElse(o.getClient().getCompanyName())))
                .filter(o -> o.getCreationDateTime().isAfter(Optional.ofNullable(creationDateTimeFrom).orElse(o.getCreationDateTime().minusSeconds(1L))))
                .filter(o -> o.getCreationDateTime().isBefore(Optional.ofNullable(creationDateTimeTo).orElse(o.getCreationDateTime().plusSeconds(1L))))
                .filter(o -> o.getState() == Optional.ofNullable(state).orElse(o.getState()))
                .filter(o -> o.getLines().stream().map(OrderLine::getRoll).anyMatch(r -> r.getSku().equals(Optional.ofNullable(rollSku).orElse(r.getSku()))))
                .map(Order::new).collect(Collectors.toList());
    }

    @Override
    public Order createOrder(@Nullable LocalDateTime creationDateTime, @NotNull Client client, @NotNull List<OrderLine> lines) throws WebServiceException {
        Order newOrder = new Order(count.incrementAndGet(),
                creationDateTime != null ? creationDateTime : LocalDateTime.now(),
                client,
                OrderState.NEW,
                lines);
        remoteOrderRegistry.add(newOrder);
        return new Order(newOrder);
    }

    @Override
    public boolean cancelOrderById(@NotNull Long id) throws WebServiceException {
        Order orderToRemove = findOrderById(id);
        orderToRemove.setState(OrderState.CANCELED);
        orderToRemove.getLines().forEach(line -> line.setState(OrderState.CANCELED));
        Order oldOrder = findOrderById(id);
        remoteOrderRegistry.set(remoteOrderRegistry.indexOf(oldOrder), orderToRemove);
        return true;
    }

    @Override
    public Order updateOrder(@NotNull Order order) throws WebServiceException {
        Order oldOrder = findOrderById(order.getId());
        remoteOrderRegistry.set(remoteOrderRegistry.indexOf(oldOrder), order);
        return new Order(order);
    }
}
