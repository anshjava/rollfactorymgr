package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.kamuzta.rollfactorymgr.GuiceJUnitRunner;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.model.roll.Roll;
import ru.kamuzta.rollfactorymgr.modules.EventBusModule;
import ru.kamuzta.rollfactorymgr.processor.OrderProcessor;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Slf4j
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({EventBusModule.class})
public class OrderProcessorTest {
    JsonUtil jsonUtil = JsonUtil.getInstance();

    @Inject
    OrderProcessor orderProcessor;

    @Before
    public void before() {
        orderProcessor.updateRegistryFromServer();
    }

    @After
    public void after() {

    }


    /**
     * Testing obtaining order registry
     */
    @Test
    public void getOrderRegistryTest() {
        log.info("_________ START getOrderRegistryTest _________");
        List<Order> orderRegistry = orderProcessor.getLocalRegistry();
        assertNotNull(orderRegistry);
        assertFalse(orderRegistry.isEmpty());
        Collections.sort(orderRegistry);
        orderRegistry.forEach(order -> log.info(order.toString()));
    }


    /**
     * Testing find order by id
     */
    @Test
    public void findOrderByIdTest() {
        log.info("_________ START findOrderByIdTest _________");
        Order order1 = orderProcessor.findOrderById(4L);
        assertNotNull(order1);
        log.info(order1.toString());

        Order order2 = orderProcessor.findOrderById(5L);
        assertNotNull(order2);
        log.info(order2.toString());

        Order order3 = null;
        try {
            order3 = orderProcessor.findOrderById(200L);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(order3);
    }

    /**
     * Testing find order by part of company's name
     */
    @Test
    public void findOrderByCompanyNamePatternTest() {
        log.info("_________ START findOrderByCompanyNamePatternTest _________");

        List<Order> result1 = orderProcessor.findOrderByCompanyNamePattern("Mega");
        assertNotNull(result1);
        result1.forEach(order -> log.info(order.toString()));
        assertEquals(12, result1.size());

        List<Order> result2 = orderProcessor.findOrderByCompanyNamePattern("Best");
        assertNotNull(result2);
        result2.forEach(order -> log.info(order.toString()));
        assertEquals(18, result2.size());

        List<Order> result3 = orderProcessor.findOrderByCompanyNamePattern("Sony");
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    /**
     * Testing finding orders by collection of params
     */
    @Test
    public void findOrderByParamsTest() {
        System.out.println("_________ START findOrderByParamsTest _________");

        List<Order> result1 = orderProcessor.findOrderByParams(21L, null, null, null, null, null);
        assertNotNull(result1);
        log.info("Result1:");
        result1.forEach(order -> log.info(order.toString()));
        assertEquals(1, result1.size());

        List<Order> result2 = orderProcessor.findOrderByParams(null, "Mega", null, null, null, null);
        assertNotNull(result2);
        log.info("Result2:");
        result2.forEach(order -> log.info(order.toString()));
        assertEquals(12, result2.size());

        List<Order> result3 = orderProcessor.findOrderByParams(null, null, OffsetDateTime.of(2022, 4, 23, 23, 14, 18, 0, ZoneOffset.of("+03:00")), null, null, null);
        assertNotNull(result3);
        log.info("Result3:");
        result3.forEach(order -> log.info(order.toString()));
        assertEquals(3, result3.size());

        List<Order> result4 = orderProcessor.findOrderByParams(null, null, null, OffsetDateTime.of(1995, 4, 10, 23, 14, 19, 0, ZoneOffset.of("+03:00")), null, null);
        assertNotNull(result4);
        log.info("Result4:");
        result4.forEach(order -> log.info(order.toString()));
        assertEquals(2, result4.size());

        List<Order> result5 = orderProcessor.findOrderByParams(null, null, OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.of("+03:00")), OffsetDateTime.of(2020, 1, 1, 0, 0, 4, 4, ZoneOffset.of("+03:00")), null, null);
        assertNotNull(result5);
        log.info("Result5:");
        result5.forEach(order -> log.info(order.toString()));
        assertEquals(3, result5.size());

        List<Order> result6 = orderProcessor.findOrderByParams(null, null, null, null, OrderState.INPROGRESS, null);
        assertNotNull(result6);
        log.info("Result6:");
        result6.forEach(order -> log.info(order.toString()));
        assertEquals(3, result6.size());

        List<Order> result7 = orderProcessor.findOrderByParams(null, null, null, null, null, "LEN8050");
        assertNotNull(result7);
        log.info("Result7:");
        result7.forEach(order -> log.info(order.toString()));
        assertEquals(10, result7.size());
    }

    /**
     * Testing removing order by id
     */
    @Test
    public void removeOrderByIdTest() {
        log.info("_________ START removeOrderByIdTest _________");

        long countBefore = orderProcessor.getLocalRegistry().stream()
                .filter(o -> o.getState() == OrderState.CANCELED).count();

        assertTrue(orderProcessor.removeOrderById(4L));

        //try to remove same order again
        try {
            assertFalse(orderProcessor.removeOrderById(4L));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }

        //try to remove order that is in workflow
        try {
            assertFalse(orderProcessor.removeOrderById(5L));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }

        //50+1=51
        long countAfter = orderProcessor.getLocalRegistry().stream()
                .filter(o -> o.getState() == OrderState.CANCELED).count();
        assertEquals(countBefore + 1, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

    /**
     * Testing creating new order
     */
    @Test
    public void createOrderTest() {
        log.info("_________ START createClientTest _________");

        int countBefore = orderProcessor.getLocalRegistry().size();

        Client activeClient = jsonUtil.getObjectFromJson("activeClient.json", Client.class, CouldNotDeserializeJsonException::new);
        List<OrderLine> lines = jsonUtil.getListFromJson("orderLineExampleList.json", OrderLine.class, CouldNotDeserializeJsonException::new);
        Client unknownClient = jsonUtil.getObjectFromJson("unknownClient.json", Client.class, CouldNotDeserializeJsonException::new);
        Roll unknownRoll = jsonUtil.getObjectFromJson("unknownRoll.json", Roll.class, CouldNotDeserializeJsonException::new);


        //date from future
        Order order1 = null;
        try {
            order1 = orderProcessor.createOrder(OffsetDateTime.now().plusDays(1), activeClient, lines);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(order1);

        //same client has equal order that is not completed
        Order inProgressOrder = orderProcessor.findOrderByCompanyNamePattern(activeClient.getCompanyName()).stream()
                .filter(o -> activeClient.equals(o.getClient()) && o.getState() == OrderState.NEW).findFirst().orElseThrow(() -> new IllegalArgumentException("Opps"));
        Order order2 = null;
        try {
            order2 = orderProcessor.createOrder(null, inProgressOrder.getClient(), inProgressOrder.getLines());
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(order2);

        //orderLines is empty
        Order order3 = null;
        try {
            order3 = orderProcessor.createOrder(null, activeClient, new ArrayList<>());
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(order3);

        //has line with wrong quantity
        lines.get(0).setQuantity(0);
        Order order4 = null;
        try {
            order4 = orderProcessor.createOrder(null, activeClient, lines);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(order4);
        lines.remove(0);

        //has unknown roll
        lines.add(0, new OrderLine(1L, unknownRoll, 10, OrderState.NEW));
        Order order5 = null;
        try {
            order5 = orderProcessor.createOrder(null, activeClient, lines);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(order5);
        lines.remove(0);

        //has unknown client
        Order order6 = null;
        try {
            order6 = orderProcessor.createOrder(null, unknownClient, lines);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(order6);

        //create good new orders
        lines.remove(11);
        Order order7 = orderProcessor.createOrder(OffsetDateTime.of(1997, 2, 18, 15, 30, 0, 0, ZoneOffset.of("+03:00")), activeClient, lines);
        assertNotNull(order7);
        List<OrderLine> copyOrderLines = lines.stream().map(OrderLine::new).collect(Collectors.toList());
        copyOrderLines.remove(11);
        Order order8 = orderProcessor.createOrder(OffsetDateTime.of(1997, 2, 18, 15, 30, 0, 0, ZoneOffset.of("+03:00")), activeClient, copyOrderLines);
        assertNotNull(order8);


        int countAfter = orderProcessor.getLocalRegistry().size();

        assertEquals(countBefore + 2, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }


    /**
     * Testing updating order parameters
     */
    @Test
    public void updateOrderTest() {
        log.info("_________ START updateOrderTest _________");

        int countBefore = orderProcessor.getLocalRegistry().size();

        Order orderInNewState = jsonUtil.getObjectFromJson("orderInNewState.json", Order.class, CouldNotDeserializeJsonException::new);
        Order orderInProgressState = jsonUtil.getObjectFromJson("orderInProgressState.json", Order.class, CouldNotDeserializeJsonException::new);
        Client unknownClient = jsonUtil.getObjectFromJson("unknownClient.json", Client.class, CouldNotDeserializeJsonException::new);
        Roll unknownRoll = jsonUtil.getObjectFromJson("unknownRoll.json", Roll.class, CouldNotDeserializeJsonException::new);

        //wrong order id
        Order clonedOrder1 = new Order(orderInNewState);
        clonedOrder1.setId(200L);
        Order resultOrder1 = null;
        try {
            resultOrder1 = orderProcessor.updateOrder(clonedOrder1);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(resultOrder1);

        //order is in progress, completed or canceled
        Order clonedOrder2 = new Order(orderInProgressState);
        clonedOrder2.getLines().get(0).setQuantity(100);
        Order resultOrder2 = null;
        try {
            resultOrder2 = orderProcessor.updateOrder(clonedOrder2);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(resultOrder2);

        //orderLines is empty
        Order clonedOrder3 = new Order(orderInNewState);
        clonedOrder3.setLines(new ArrayList<>());
        Order resultOrder3 = null;
        try {
            resultOrder3 = orderProcessor.updateOrder(clonedOrder3);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(resultOrder3);

        //has line with wrong quantity
        Order clonedOrder4 = new Order(orderInNewState);
        clonedOrder4.getLines().get(0).setQuantity(0);
        Order resultOrder4 = null;
        try {
            resultOrder4 = orderProcessor.updateOrder(clonedOrder4);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(resultOrder4);

        //has unknown roll
        Order clonedOrder5 = new Order(orderInNewState);
        clonedOrder5.getLines().get(0).setRoll(unknownRoll);
        Order resultOrder5 = null;
        try {
            resultOrder5 = orderProcessor.updateOrder(clonedOrder5);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(resultOrder5);

        //has unknown client
        Order clonedOrder6 = new Order(orderInNewState);
        clonedOrder6.setClient(unknownClient);
        Order resultOrder6 = null;
        try {
            resultOrder6 = orderProcessor.updateOrder(clonedOrder6);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(resultOrder6);

        //same client has equal to new modified order that is not completed
        Order clonedOrder7 = new Order(orderInNewState);
        Order resultOrder7 = null;
        try {
            resultOrder7 = orderProcessor.updateOrder(clonedOrder7);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(resultOrder7);

        //update successfully
        List<Order> orderList = orderProcessor.getLocalRegistry();

        Order order8 = orderList.get(0);
        Order order8Cloned = new Order(order8);
        order8Cloned.setCreationDate(OffsetDateTime.now());
        Order order8AfterUpdate = orderProcessor.updateOrder(order8Cloned);
        assertNotNull(order8AfterUpdate);
        assertEquals(order8Cloned.getCreationDate(), order8AfterUpdate.getCreationDate());
        Order order9 = orderList.get(1);
        Order order9Cloned = new Order(order9);
        order9Cloned.getLines().get(0).setQuantity(100);
        Order order9AfterUpdate = orderProcessor.updateOrder(order9Cloned);
        assertNotNull(order9AfterUpdate);
        assertEquals(order9Cloned.getLines(), order9AfterUpdate.getLines());

        int countAfter = orderProcessor.getLocalRegistry().size();
        assertEquals(countBefore, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

}
