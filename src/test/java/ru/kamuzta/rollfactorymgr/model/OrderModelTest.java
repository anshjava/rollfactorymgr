package ru.kamuzta.rollfactorymgr.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.utils.TestUtils;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotSeserializeToJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

@Slf4j
public class OrderModelTest {
    JsonUtil jsonUtil = JsonUtil.getInstance();

    @Before
    public void before() {

    }

    /**
     * Testing Order cloning
     */
    @Test
    public void orderGetCloneTest() {
        log.info("_________ START orderGetCloneTest _________");
        Order order = TestUtils.getRandomOrder();
        log.info(order.toString());
        Order clonedOrder = new Order(order);
        log.info(clonedOrder.toString());
        assertEquals(order, clonedOrder);
        assertNotSame(order, clonedOrder);
    }

    /**
     * Testing Order sort
     */
    @Test
    public void orderCompareTest() {
        log.info("_________ START orderCompareTest _________");
        TreeSet<Order> orderSet = new TreeSet<>();
        for (int i = 0; i < 100; i++) {
            orderSet.add(TestUtils.getRandomOrder());
        }
        orderSet.forEach(order -> log.info(order.toString()));
    }

    /**
     * Try serialization and deserialization of order
     */
    @Test
    public void orderSerializationTest() {
        log.info("_________ START orderSerializationTest _________");
        Order order1 = Order.builder()
                .id(1L)
                .creationDateTime(LocalDateTime.now().withNano(0))
                .client(TestUtils.getRandomClient())
                .lines(TestUtils.getRandomOrderLineList(5))
                .state(OrderState.NEW)
                .build();
        log.info(order1.toString());
        String json = jsonUtil.writeObject(order1, CouldNotSeserializeToJsonException::new);
        Order order2 = jsonUtil.readValue(json, Order.class, CouldNotDeserializeJsonException::new);
        log.info(order2.toString());
        assertEquals(order1, order2);
        assertNotSame(order1, order2);
    }

    /**
     * Create example orders
     */
    @Test
    public void orderListExampleCreateTest() {
        log.info("_________ START orderListExampleCreateTest _________");
        List<Order> orderList = new ArrayList<>();
        List<Client> clientListFromJson = jsonUtil.getListFromJson("clientRegistry.json", Client.class, CouldNotDeserializeJsonException::new);
        List<OrderLine> orderLineListFromJson = jsonUtil.getListFromJson("orderLineExampleList.json", OrderLine.class, CouldNotDeserializeJsonException::new);

        for (int i = 1; i <= 100; i++) {
            int linesCount = Math.max(1, TestUtils.getRandom().nextInt(5));
            Order order = Order.builder()
                    .id((long) i)
                    .creationDateTime(LocalDateTime.now())
                    .client(TestUtils.getRandomElementFromList(clientListFromJson))
                    .lines(TestUtils.getRandomElementsFromList(orderLineListFromJson, linesCount).stream().map(OrderLine::new).collect(Collectors.toList()))
                    .state(OrderState.NEW)
                    .build();
            orderList.add(order);
        }
        //states(for testing purpose)
        IntStream.rangeClosed(0,1).forEach(i -> {
            OrderState state = OrderState.NEW;
            Order order = orderList.get(i);
            order.setState(state);
            order.getLines().forEach(line -> line.setState(state));
        });
        IntStream.rangeClosed(2,3).forEach(i -> {
            OrderState state = OrderState.QUEUED;
            Order order = orderList.get(i);
            order.setState(state);
            order.getLines().forEach(line -> line.setState(state));
        });
        IntStream.rangeClosed(4,5).forEach(i -> {
            OrderState state = OrderState.INPROGRESS;
            Order order = orderList.get(i);
            order.setState(state);
            order.getLines().forEach(line -> line.setState(state));
        });
        IntStream.rangeClosed(6,49).forEach(i -> {
            OrderState state = OrderState.COMPLETED;
            Order order = orderList.get(i);
            order.setState(state);
            order.getLines().forEach(line -> line.setState(state));
        });
        IntStream.rangeClosed(50,99).forEach(i -> {
            OrderState state = OrderState.CANCELED;
            Order order = orderList.get(i);
            order.setState(state);
            order.getLines().forEach(line -> line.setState(state));
        });

        String json = jsonUtil.writeObject(orderList, CouldNotDeserializeJsonException::new);
        assertNotNull(json);
        log.info(json);
    }

    /**
     * Testing reading jsonOrder to list
     */
    @Test
    public void orderListExampleReadFromJsonTest() {
        log.info("_________ START orderListExampleReadFromJsonTest _________");
        List<Order> orderListFromJson = jsonUtil.getListFromJson("orderRegistry.json", Order.class, CouldNotDeserializeJsonException::new);
        assertNotNull(orderListFromJson);
        assertFalse(orderListFromJson.isEmpty());
        Collections.sort(orderListFromJson);
        orderListFromJson.forEach(orderLine -> log.info(orderLine.toString()));
        orderListFromJson.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        String json = jsonUtil.writeObject(orderListFromJson, CouldNotDeserializeJsonException::new);
        log.info(json);
    }

}
