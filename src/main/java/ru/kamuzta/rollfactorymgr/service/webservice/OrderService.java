package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;

import java.time.OffsetDateTime;
import java.util.List;

@ImplementedBy(OrderServiceMock.class)
public interface OrderService {
    /**
     * Update local cached Registry by values from server
     *
     * @throws WebServiceException
     */
    void updateRegistryFromServer() throws WebServiceException;

    /**
     * Get local cached Registry
     *
     * @return local cached Registry
     * @throws WebServiceException
     */
    List<Order> getLocalRegistry();

    /**
     * Find Order by id in local cached Registry
     *
     * @param id id of Order
     * @return found Order
     * @throws WebServiceException
     */
    Order findOrderById(@NotNull Long id) throws WebServiceException;

    /**
     * Find Order by companyName pattern in local cached Registry
     *
     * @param companyName name/part of name of Client order owner
     * @return list of matched Orders
     * @throws WebServiceException
     */
    List<Order> findOrderByCompanyNamePattern(@NotNull String companyName) throws WebServiceException;

    /**
     * Find Order by parameters in local cached Registry
     *
     * @param id               part of order id
     * @param companyName      part of client's company name
     * @param creationDateFrom order creation date from
     * @param creationDateTo   order creation date to
     * @param state            order state
     * @param lines            order lines
     * @return list of matched Orders
     * @throws WebServiceException
     */
    List<Order> findOrderByParams(@Nullable Long id, @Nullable String companyName, @Nullable OffsetDateTime creationDateFrom, @Nullable OffsetDateTime creationDateTo, @Nullable OrderState state, @Nullable List<OrderLine> lines) throws WebServiceException;

    /**
     * Create Order on Server Registry
     *
     * @param creationDate dateTime of creation
     * @param client       client
     * @param lines        order lines
     * @return new Order
     * @throws WebServiceException if validation fail
     */
    Order createOrder(@Nullable OffsetDateTime creationDate, @NotNull Client client, @NotNull List<OrderLine> lines) throws WebServiceException;

    /**
     * Remove Order on Server Registry by id
     *
     * @param id id of Order to remove
     * @return true if success
     * @throws WebServiceException if Order with specified id was not found or it is producing right now
     */
    boolean removeOrderById(@NotNull Long id) throws WebServiceException;

    /**
     * Update Order on Server Registry with new parameters
     *
     * @param order - Order with same id but diffirent parameters
     * @return updated Order
     * @throws WebServiceException if validation fail or all parameters are equals
     */
    Order updateOrder(@NotNull Order order) throws WebServiceException;

}
