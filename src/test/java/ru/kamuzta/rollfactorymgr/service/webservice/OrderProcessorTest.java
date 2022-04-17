package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.kamuzta.rollfactorymgr.GuiceJUnitRunner;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.modules.EventBusModule;
import ru.kamuzta.rollfactorymgr.processor.OrderProcessor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({EventBusModule.class})
public class OrderProcessorTest {

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
            order3 = orderProcessor.findOrderById(101L);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(order3);
    }

    /**
     * Testing find order by part of company's name
     */
    @Test
    public void findOrderByCompanyNamePatternTest() {
        System.out.println("_________ START findOrderByCompanyNamePatternTest _________");

        List<Order> result1 = orderProcessor.findOrderByCompanyNamePattern("Roll");
        assertNotNull(result1);
        result1.forEach(order -> log.info(order.toString()));
        assertEquals(56, result1.size());

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

        List<Order> result2 = orderProcessor.findOrderByParams(null, "Roll", null, null, null, null);
        assertNotNull(result2);
        log.info("Result2:");
        result2.forEach(order -> log.info(order.toString()));
        assertEquals(56, result2.size());

        List<Order> result3 = orderProcessor.findOrderByParams(null, null, OffsetDateTime.of(2022, 4, 10, 23, 14, 18, 0, ZoneOffset.of("+03:00")), null, null, null);
        assertNotNull(result3);
        log.info("Result3:");
        result3.forEach(order -> log.info(order.toString()));
        assertEquals(93, result3.size());


        List<Order> result4 = orderProcessor.findOrderByParams(null, null, null, OffsetDateTime.of(2021, 4, 10, 23, 14, 19, 0, ZoneOffset.of("+03:00")), null, null);
        assertNotNull(result4);
        log.info("Result4:");
        result4.forEach(order -> log.info(order.toString()));
        assertEquals(7, result4.size());

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

        List<Order> result7 = orderProcessor.findOrderByParams(null, null, null, null, null, "LEN5719");
        assertNotNull(result7);
        log.info("Result7:");
        result7.forEach(order -> log.info(order.toString()));
        assertEquals(14, result7.size());
    }
//
//    /**
//     * Testing removing client by id
//     */
//    @Test
//    public void removeClientByIdTest() {
//        System.out.println("_________ START removeClientByIdTest _________");
//
//        int countBefore = orderProcessor.getLocalRegistry().size();
//        assertTrue(orderProcessor.removeClientById(5L));
//
//        //try to delete same client again
//        try {
//            assertFalse(orderProcessor.removeClientById(5L));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//
//        //try to delete client that is in workflow
//        try {
//            assertFalse(orderProcessor.removeClientById(1L));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//
//        //9-1=8
//        int countAfter = orderProcessor.getLocalRegistry().size();
//        assertEquals(countBefore - 1, countAfter);
//        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
//    }
//
//    /**
//     * Testing creating new client
//     */
//    @Test
//    public void createClientTest() {
//        System.out.println("_________ START createClientTest _________");
//
//        int countBefore = orderProcessor.getLocalRegistry().size();
//
//        //same companyName
//        Client client1 = null;
//        try {
//            client1 = orderProcessor.createClient(null, "OptTrade", "Bobruysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client1);
//
//        //wrong companyName format
//        Client client2 = null;
//        try {
//            client2 = orderProcessor.createClient(null, "~!cawabanga!~", "Bobruysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client2);
//
//        //wrong city format
//        Client client3 = null;
//        try {
//            client3 = orderProcessor.createClient(null, "Cawabanga", "!~Bobriysk<>", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client3);
//
//        //wrong address format
//        Client client4 = null;
//        try {
//            client4 = orderProcessor.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45!", "Vladimir", "79140789975", "vladimir@yandex.ru");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client4);
//
//        //wrong buyerName format
//        Client client5 = null;
//        try {
//            client5 = orderProcessor.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir2", "79140789975", "vladimir@yandex.ru");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client5);
//
//        //wrong phone format
//        Client client6 = null;
//        try {
//            client6 = orderProcessor.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir", "+79140789975", "vladimir@yandex.ru");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client6);
//
//        //wrong email format
//        Client client7 = null;
//        try {
//            client7 = orderProcessor.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru.");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client7);
//
//        //creating duplicate client
//        Client client8 = null;
//        try {
//            client8 = orderProcessor.createClient(null, "NinjaRoll", "Tokyo", "Naruto 33", "Naruto Naruto", "71234567889", "naruto@tokyo.jp");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client8);
//
//        //create good new clients
//        Client client9 = orderProcessor.createClient(null, "RollCompany", "Monaco", "BigStreet 1", "Paul", "79140782275", "paul@monaco.com");
//        assertNotNull(client9);
//        Client client10 = orderProcessor.createClient(null, "MyRoll", "Barselona", "Travaha 15", "Antonio", "79122789915", "antonio@travaha.es");
//        assertNotNull(client10);
//
//        int countAfter = orderProcessor.getLocalRegistry().size();
//
//        assertEquals(countBefore + 2, countAfter);
//        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
//    }
//
//
//    /**
//     * Testing updating client parameters
//     */
//    @Test
//    public void updateClientTest() {
//        System.out.println("_________ START updateClientTest _________");
//
//        int countBefore = orderProcessor.getLocalRegistry().size();
//
//        //try to change client that is not existed
//        Client client1 = orderProcessor.findClientById(1L);
//        assertNotNull(client1);
//        System.out.println(client1);
//        client1.setId(100L);
//        Client client1AfterUpdate = null;
//        try {
//            client1AfterUpdate = orderProcessor.updateClient(client1);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client1AfterUpdate);
//
//        //try to change client that is in workflow
//        Client client2 = orderProcessor.findClientById(7L);
//        assertNotNull(client2);
//        System.out.println(client2);
//        client2.setCity("St.Petersburg");
//        Client client2AfterUpdate = null;
//        try {
//            client2AfterUpdate = orderProcessor.updateClient(client2);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client2AfterUpdate);
//
//        //try to change client that has same parameters of another client
//        Client client3 = orderProcessor.findClientById(3L);
//        assertNotNull(client3);
//        System.out.println(client3);
//        client3.setId(30L);
//        Client client3AfterUpdate = null;
//        try {
//            client3AfterUpdate = orderProcessor.updateClient(client3);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client3AfterUpdate);
//
//        //try to change client with invalid city
//        Client client4 = orderProcessor.findClientById(4L);
//        assertNotNull(client4);
//        System.out.println(client4);
//        client4.setCity("798");
//        Client client4AfterUpdate = null;
//        try {
//            client4AfterUpdate = orderProcessor.updateClient(client4);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client4AfterUpdate);
//
//        //try to change client with invalid address
//        Client client5 = orderProcessor.findClientById(5L);
//        assertNotNull(client5);
//        System.out.println(client5);
//        client5.setAddress("!~Address");
//        Client client5AfterUpdate = null;
//        try {
//            client5AfterUpdate = orderProcessor.updateClient(client5);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client5AfterUpdate);
//
//        //try to change client with invalid buyerName
//        Client client6 = orderProcessor.findClientById(6L);
//        assertNotNull(client6);
//        System.out.println(client6);
//        client6.setBuyerName("Buyer88");
//        Client client6AfterUpdate = null;
//        try {
//            client6AfterUpdate = orderProcessor.updateClient(client6);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client6AfterUpdate);
//
//        //try to change client with invalid buyer phone
//        Client client7 = orderProcessor.findClientById(8L);
//        assertNotNull(client7);
//        System.out.println(client7);
//        client7.setPhone("875245698745");
//        Client client7AfterUpdate = null;
//        try {
//            client7AfterUpdate = orderProcessor.updateClient(client7);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client7AfterUpdate);
//
//        //try to change client with invalid buyer email
//        Client client8 = orderProcessor.findClientById(9L);
//        assertNotNull(client8);
//        System.out.println(client8);
//        client8.setEmail("mymail!@ggdkm.ru");
//        Client client8AfterUpdate = null;
//        try {
//            client8AfterUpdate = orderProcessor.updateClient(client8);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            assertTrue(e instanceof WebServiceException);
//        }
//        assertNull(client8AfterUpdate);
//
//        //update successfully
//        Client client9 = orderProcessor.findClientById(2L);
//        assertNotNull(client9);
//        System.out.println(client9);
//        Client client9Cloned = new Client(client9);
//        client9Cloned.setEmail("newmail@newdomail.new");
//        Client client9AfterUpdate = orderProcessor.updateClient(client9Cloned);
//        assertNotNull(client9AfterUpdate);
//        System.out.println(client9AfterUpdate);
//        assertNotNull(client9AfterUpdate);
//        assertEquals(client9Cloned.getEmail(), client9AfterUpdate.getEmail());
//        assertNotEquals(client9, client9AfterUpdate);
//
//        int countAfter = orderProcessor.getLocalRegistry().size();
//        assertEquals(countBefore, countAfter);
//        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
//    }

}
