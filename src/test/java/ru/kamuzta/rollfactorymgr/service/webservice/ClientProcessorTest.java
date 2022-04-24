package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.kamuzta.rollfactorymgr.GuiceJUnitRunner;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.modules.EventBusModule;
import ru.kamuzta.rollfactorymgr.processor.ClientProcessor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({EventBusModule.class})
public class ClientProcessorTest {

    @Inject
    ClientProcessor clientProcessor;

    @Before
    public void before() {
        clientProcessor.updateRegistryFromServer();
    }

    /**
     * Testing obtaining client registry
     */
    @Test
    public void getClientRegistryTest() {
        log.info("_________ START getClientRegistryTest _________");
        List<Client> clientRegistry = clientProcessor.getActiveClientsLocal();
        assertNotNull(clientRegistry);
        assertFalse(clientRegistry.isEmpty());
        Collections.sort(clientRegistry);
        clientRegistry.forEach(client -> {
            log.info(client.toString());
            assertEquals(ClientState.ACTIVE, client.getState());
        });
    }

    /**
     * Testing find client by id
     */
    @Test
    public void findClientByIdTest() {
        log.info("_________ START findClientByIdTest _________");
        Client client1 = clientProcessor.findClientById(4L);
        assertNotNull(client1);
        log.info(client1.toString());

        Client client2 = clientProcessor.findClientById(5L);
        assertNotNull(client2);
        log.info(client2.toString());

        Client client3 = null;
        try {
            client3 = clientProcessor.findClientById(9L);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client3);
    }

    /**
     * Testing find client by part of name
     */
    @Test
    public void findClientByNamePatternTest() {
        log.info("_________ START findClientByNamePatternTest _________");

        List<Client> result1 = clientProcessor.findClientByNamePattern("Roll");
        assertNotNull(result1);
        result1.forEach(System.out::println);
        assertEquals(4, result1.size());

        List<Client> result2 = clientProcessor.findClientByNamePattern("Best");
        assertNotNull(result2);
        result2.forEach(System.out::println);
        assertEquals(2, result2.size());

        List<Client> result3 = clientProcessor.findClientByNamePattern("Sony");
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    /**
     * Testing finding client by collection of params
     */
    @Test
    public void findClientByParamsTest() {
        log.info("_________ START findClientByParamsTest _________");

        List<Client> result1 = clientProcessor.findClientByParams(2L, null, null, null, null, null, null, null, null);
        assertNotNull(result1);
        log.info("Result1:");
        result1.forEach(client -> log.info(client.toString()));
        assertEquals(1, result1.size());

        List<Client> result2 = clientProcessor.findClientByParams(null, "Roll", null, null, null, null, null, null, null);
        assertNotNull(result2);
        log.info("Result2:");
        result2.forEach(client -> log.info(client.toString()));
        assertEquals(4, result2.size());

        List<Client> result3 = clientProcessor.findClientByParams(null, null, LocalDate.of(2019, 12, 31), null, null, null, null, "7800", null);
        assertNotNull(result3);
        log.info("Result3:");
        result3.forEach(client -> log.info(client.toString()));
        assertEquals(2, result3.size());

        List<Client> result4 = clientProcessor.findClientByParams(null, null, null, LocalDate.of(1997, 3, 6), null, null, null, null, null);
        assertNotNull(result4);
        log.info("Result4:");
        result4.forEach(client -> log.info(client.toString()));
        assertEquals(3, result4.size());

        List<Client> result5 = clientProcessor.findClientByParams(null, null, LocalDate.of(2009, 12, 31), LocalDate.of(2012, 3, 4), null, null, null, null, null);
        assertNotNull(result5);
        log.info("Result5:");
        result5.forEach(client -> log.info(client.toString()));
        assertEquals(3, result5.size());

        List<Client> result6 = clientProcessor.findClientByParams(null, null, null, null, "Los", null, null, null, null);
        assertNotNull(result6);
        log.info("Result6:");
        result6.forEach(client -> log.info(client.toString()));
        assertEquals(3, result6.size());

        List<Client> result7 = clientProcessor.findClientByParams(null, null, null, null, null, "Naruto", null, null, null);
        assertNotNull(result7);
        log.info("Result7:");
        result7.forEach(client -> log.info(client.toString()));
        assertEquals(1, result7.size());
    }

    /**
     * Testing removing client by id
     */
    @Test
    public void removeClientByIdTest() {
        log.info("_________ START removeClientByIdTest _________");

        int countBefore = clientProcessor.getActiveClientsLocal().size();
        assertEquals(10, countBefore);
        assertTrue(clientProcessor.removeClientById(6L));

        //try to delete same client again
        try {
            assertFalse(clientProcessor.removeClientById(6L));
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }

        //try to delete client that is in workflow
        try {
            assertFalse(clientProcessor.removeClientById(1L));
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }

        //10-1=9
        int countAfter = clientProcessor.getActiveClientsLocal().size();
        assertEquals(9, countAfter);
        assertEquals(countBefore - 1, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

    /**
     * Testing creating new client
     */
    @Test
    public void createClientTest() {
        log.info("_________ START createClientTest _________");

        int countBefore = clientProcessor.getActiveClientsLocal().size();

        //same companyName
        Client client1 = null;
        try {
            client1 = clientProcessor.createClient(null, "OptTrade", "Bobruysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client1);

        //wrong companyName format
        Client client2 = null;
        try {
            client2 = clientProcessor.createClient(null, "~!cawabanga!~", "Bobruysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client2);

        //wrong city format
        Client client3 = null;
        try {
            client3 = clientProcessor.createClient(null, "Cawabanga", "!~Bobriysk<>", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client3);

        //wrong address format
        Client client4 = null;
        try {
            client4 = clientProcessor.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45!", "Vladimir", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client4);

        //wrong buyerName format
        Client client5 = null;
        try {
            client5 = clientProcessor.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir2", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client5);

        //wrong phone format
        Client client6 = null;
        try {
            client6 = clientProcessor.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir", "+79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client6);

        //wrong email format
        Client client7 = null;
        try {
            client7 = clientProcessor.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru.");
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client7);

        //creating duplicate client
        Client client8 = null;
        try {
            client8 = clientProcessor.createClient(null, "NinjaRoll", "Tokyo", "Naruto 33", "Naruto Naruto", "71234567889", "naruto@tokyo.jp");
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client8);

        //create good new clients
        Client client9 = clientProcessor.createClient(null, "RoCompany", "Monaco", "BigStreet 1", "Paul", "79140782275", "paul@monaco.com");
        assertNotNull(client9);
        Client client10 = clientProcessor.createClient(null, "MyRo", "Barselona", "Travaha 15", "Antonio", "79122789915", "antonio@travaha.es");
        assertNotNull(client10);

        int countAfter = clientProcessor.getActiveClientsLocal().size();

        assertEquals(countBefore + 2, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }


    /**
     * Testing updating client parameters
     */
    @Test
    public void updateClientTest() {
        log.info("_________ START updateClientTest _________");

        int countBefore = clientProcessor.getActiveClientsLocal().size();

        //try to change client that is not existed
        Client client1 = clientProcessor.findClientById(1L);
        assertNotNull(client1);
        log.info(client1.toString());
        client1.setId(100L);
        Client client1AfterUpdate = null;
        try {
            client1AfterUpdate = clientProcessor.updateClient(client1);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client1AfterUpdate);

        //try to change client that is in workflow
        Client client2 = clientProcessor.findClientById(2L);
        assertNotNull(client2);
        log.info(client2.toString());
        client2.setCity("St.Petersburg");
        Client client2AfterUpdate = null;
        try {
            client2AfterUpdate = clientProcessor.updateClient(client2);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client2AfterUpdate);

        //try to change client that has same parameters of another client
        Client client3 = clientProcessor.findClientById(3L);
        assertNotNull(client3);
        log.info(client3.toString());
        client3.setId(30L);
        Client client3AfterUpdate = null;
        try {
            client3AfterUpdate = clientProcessor.updateClient(client3);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client3AfterUpdate);

        //try to change client with invalid city
        Client client4 = clientProcessor.findClientById(4L);
        assertNotNull(client4);
        log.info(client4.toString());
        client4.setCity("798");
        Client client4AfterUpdate = null;
        try {
            client4AfterUpdate = clientProcessor.updateClient(client4);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client4AfterUpdate);

        //try to change client with invalid address
        Client client5 = clientProcessor.findClientById(5L);
        assertNotNull(client5);
        log.info(client5.toString());
        client5.setAddress("!~Address");
        Client client5AfterUpdate = null;
        try {
            client5AfterUpdate = clientProcessor.updateClient(client5);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client5AfterUpdate);

        //try to change client with invalid buyerName
        Client client6 = clientProcessor.findClientById(6L);
        assertNotNull(client6);
        log.info(client6.toString());
        client6.setBuyerName("Buyer88");
        Client client6AfterUpdate = null;
        try {
            client6AfterUpdate = clientProcessor.updateClient(client6);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client6AfterUpdate);

        //try to change client with invalid buyer phone
        Client client7 = clientProcessor.findClientById(8L);
        assertNotNull(client7);
        log.info(client7.toString());
        client7.setPhone("875245698745");
        Client client7AfterUpdate = null;
        try {
            client7AfterUpdate = clientProcessor.updateClient(client7);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client7AfterUpdate);

        //try to change client with invalid buyer email
        Client client8 = clientProcessor.findClientById(8L);
        assertNotNull(client8);
        log.info(client8.toString());
        client8.setEmail("mymail!@ggdkm.ru");
        Client client8AfterUpdate = null;
        try {
            client8AfterUpdate = clientProcessor.updateClient(client8);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(client8AfterUpdate);

        //update successfully
        Client client9 = clientProcessor.findClientById(7L);
        assertNotNull(client9);
        log.info(client9.toString());
        Client client9Cloned = new Client(client9);
        client9Cloned.setEmail("newmail@newdomail.new");
        Client client9AfterUpdate = clientProcessor.updateClient(client9Cloned);
        assertNotNull(client9AfterUpdate);
        log.info(client9AfterUpdate.toString());
        assertNotNull(client9AfterUpdate);
        assertEquals(client9Cloned.getEmail(), client9AfterUpdate.getEmail());
        assertNotEquals(client9, client9AfterUpdate);

        int countAfter = clientProcessor.getActiveClientsLocal().size();
        assertEquals(countBefore, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

}
