package ru.kamuzta.rollfactorymgr.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.utils.TestUtils;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotSeserializeToJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

@Slf4j
public class OrderLineModelTest {
    JsonUtil jsonUtil = JsonUtil.getInstance();

    @Before
    public void before() {

    }

    /**
     * Testing orderLine cloning
     */
    @Test
    public void orderLineGetCloneTest() {
        log.info("_________ START orderLineGetCloneTest _________");
        OrderLine orderLine = TestUtils.getRandomOrderLine();
        log.info(orderLine.toString());
        OrderLine clonedOrderLine = new OrderLine(orderLine);
        log.info(clonedOrderLine.toString());
        assertEquals(orderLine, clonedOrderLine);
        assertNotSame(orderLine, clonedOrderLine);
    }

    /**
     * Testing orderLine sort
     */
    @Test
    public void orderLineCompareTest() {
        log.info("_________ START orderLineCompareTest _________");
        TreeSet<OrderLine> orderLineSet = new TreeSet<>();
        for (int i = 0; i < 100; i++) {
            orderLineSet.add(TestUtils.getRandomOrderLine());
        }
        orderLineSet.forEach(orderLine -> log.info(orderLine.toString()));
    }

    /**
     * Try serialization and deserialization of orderLine
     */
    @Test
    public void orderLineSerializationTest() {
        log.info("_________ START orderLineSerializationTest _________");
        OrderLine orderLine1 = OrderLine.builder()
                .id(1L)
                .roll(TestUtils.getRandomRoll())
                .quantity(1000)
                .state(OrderState.NEW)
                .build();
        log.info(orderLine1.toString());
        String json = jsonUtil.writeObject(orderLine1, CouldNotSeserializeToJsonException::new);
        OrderLine orderLine2 = jsonUtil.readValue(json, OrderLine.class, CouldNotDeserializeJsonException::new);
        log.info(orderLine2.toString());
        assertEquals(orderLine1, orderLine2);
        assertNotSame(orderLine1, orderLine2);
    }

    /**
     * Create example orderLines
     */
    @Test
    public void orderLineListExampleCreateTest() {
        log.info("_________ START orderLineListExampleCreateTest _________");
        List<OrderLine> orderLineList = new ArrayList<>();
        orderLineList.add(OrderLine.builder().id(1L).roll(TestUtils.getStandardRoll(1L)).quantity(1000).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(2L).roll(TestUtils.getStandardRoll(2L)).quantity(2000).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(3L).roll(TestUtils.getStandardRoll(3L)).quantity(3000).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(4L).roll(TestUtils.getStandardRoll(4L)).quantity(4000).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(5L).roll(TestUtils.getStandardRoll(5L)).quantity(500).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(6L).roll(TestUtils.getStandardRoll(6L)).quantity(600).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(7L).roll(TestUtils.getStandardRoll(7L)).quantity(700).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(8L).roll(TestUtils.getStandardRoll(8L)).quantity(800).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(9L).roll(TestUtils.getStandardRoll(9L)).quantity(900).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(10L).roll(TestUtils.getStandardRoll(10L)).quantity(1000).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(11L).roll(TestUtils.getStandardRoll(11L)).quantity(1100).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(12L).roll(TestUtils.getStandardRoll(12L)).quantity(1200).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(13L).roll(TestUtils.getStandardRoll(13L)).quantity(1300).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(14L).roll(TestUtils.getStandardRoll(14L)).quantity(1400).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(15L).roll(TestUtils.getStandardRoll(15L)).quantity(1500).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(16L).roll(TestUtils.getStandardRoll(16L)).quantity(1600).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(17L).roll(TestUtils.getStandardRoll(17L)).quantity(1700).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(18L).roll(TestUtils.getStandardRoll(18L)).quantity(1800).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(19L).roll(TestUtils.getStandardRoll(19L)).quantity(1900).state(OrderState.NEW).build());
        orderLineList.add(OrderLine.builder().id(20L).roll(TestUtils.getStandardRoll(20L)).quantity(2000).state(OrderState.NEW).build());

        String json = jsonUtil.writeObject(orderLineList, CouldNotDeserializeJsonException::new);
        assertNotNull(json);
        log.info(json);
    }

    /**
     * Testing reading jsonOrderLines to list
     */
    @Test
    public void orderLineListExampleReadFromJsonTest() {
        log.info("_________ START orderLineListExampleReadFromJsonTest _________");
        List<OrderLine> orderLineListFromJson = jsonUtil.getListFromJson("orderLineExampleList.json", OrderLine.class, CouldNotDeserializeJsonException::new);
        assertNotNull(orderLineListFromJson);
        assertFalse(orderLineListFromJson.isEmpty());
        Collections.sort(orderLineListFromJson);
        orderLineListFromJson.forEach(orderLine -> log.info(orderLine.toString()));
    }


}
