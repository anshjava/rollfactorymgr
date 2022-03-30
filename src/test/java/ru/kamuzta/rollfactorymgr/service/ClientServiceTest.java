package ru.kamuzta.rollfactorymgr.service;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.kamuzta.rollfactorymgr.GuiceJUnitRunner;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.modules.EventBusModule;
import ru.kamuzta.rollfactorymgr.service.webservice.ClientService;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({EventBusModule.class})
public class ClientServiceTest {

    JsonUtil jsonUtil = JsonUtil.getInstance();

    @Inject
    ClientService clientService;

    @Before
    public void before() {
        clientService.updateRegistryFromServer();
        //setup test client registry before each test
        clientService.getLocalRegistry().stream().filter(client -> !client.getId().equals(1L)).forEach(client -> clientService.removeClientById(client.getId()));
        List<Client> testClients = jsonUtil.getListFromJson("clientRegistry.json", Client.class, CouldNotDeserializeJsonException::new);
        testClients.stream().filter(client -> !client.getId().equals(1L)).forEach(client -> clientService.createClient(
                client.getCreationDate(),
                client.getCompanyName(),
                client.getCity(),
                client.getAddress(),
                client.getBuyerName(),
                client.getPhone(),
                client.getEmail()));
    }

    /**
     * Testing obtaining client registry
     */
    @Test
    public void getClientRegistryTest() {
        System.out.println("_________ START getClientRegistryTest _________");
        List<Client> clientRegistry = clientService.getLocalRegistry();
        assertNotNull(clientRegistry);
        assertFalse(clientRegistry.isEmpty());
        Collections.sort(clientRegistry);
        clientRegistry.forEach(System.out::println);
    }

    /**
     * Testing find client by id
     */
    @Test
    public void findClientByIdTest() {
        System.out.println("_________ START findClientByIdTest _________");
        Client client1 = clientService.findClientById(4L);
        assertNotNull(client1);
        System.out.println(client1);

        Client client2 = clientService.findClientById(5L);
        assertNotNull(client2);
        System.out.println(client2);

        Client client3 = null;
        try {
            client3 = clientService.findClientById(100L);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client3);
    }

    /**
     * Testing find client by part of name
     */
    @Test
    public void findClientByNamePatternTest() {
        System.out.println("_________ START findClientByNamePatternTest _________");

        List<Client> result1 = clientService.findClientByNamePattern("Roll");
        assertNotNull(result1);
        result1.forEach(System.out::println);
        assertEquals(5, result1.size());

        List<Client> result2 = clientService.findClientByNamePattern("Best");
        assertNotNull(result2);
        result2.forEach(System.out::println);
        assertEquals(2, result2.size());

        List<Client> result3 = clientService.findClientByNamePattern("Sony");
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    /**
     * Testing finding client by collection of params
     */
    @Test
    public void findClientByParamsTest() {
        System.out.println("_________ START findClientByParamsTest _________");

        List<Client> result1 = clientService.findClientByParams(2L, null, null, null, null, null, null, null, null);
        assertNotNull(result1);
        System.out.println("Result1:");
        result1.forEach(System.out::println);
        assertEquals(1, result1.size());

        List<Client> result2 = clientService.findClientByParams(null, "Roll", null, null, null, null, null, null, null);
        assertNotNull(result2);
        System.out.println("Result2:");
        result2.forEach(System.out::println);
        assertEquals(5, result2.size());

        List<Client> result3 = clientService.findClientByParams(null, null, OffsetDateTime.of(2020, 1, 1, 1, 1, 0, 0, ZoneOffset.of("+09:00")), null, null, null, null, null, null);
        assertNotNull(result3);
        System.out.println("Result3:");
        result3.forEach(System.out::println);
        assertEquals(3, result3.size());


        List<Client> result4 = clientService.findClientByParams(null, null, null, OffsetDateTime.of(1997, 3, 5, 8, 59, 31, 300, ZoneOffset.of("-07:00")), null, null, null, null, null);
        assertNotNull(result4);
        System.out.println("Result4:");
        result4.forEach(System.out::println);
        assertEquals(3, result4.size());

        List<Client> result5 = clientService.findClientByParams(null, null, OffsetDateTime.of(2010, 1, 1, 1, 1, 0, 0, ZoneOffset.of("+03:00")), OffsetDateTime.of(2012, 3, 3, 3, 3, 4, 4, ZoneOffset.of("+03:00")), null, null, null, null, null);
        assertNotNull(result5);
        System.out.println("Result5:");
        result5.forEach(System.out::println);
        assertEquals(3, result5.size());

        List<Client> result6 = clientService.findClientByParams(null, null, null, null, "Los", null, null, null, null);
        assertNotNull(result6);
        System.out.println("Result6:");
        result6.forEach(System.out::println);
        assertEquals(3, result6.size());

        List<Client> result7 = clientService.findClientByParams(null, null, null, null, null, "Naruto", null, null, null);
        assertNotNull(result7);
        System.out.println("Result7:");
        result7.forEach(System.out::println);
        assertEquals(1, result7.size());
    }

    /**
     * Testing removing client by id
     */
    @Test
    public void removeClientByIdTest() {
        System.out.println("_________ START removeClientByIdTest _________");

        int countBefore = clientService.getLocalRegistry().size();
        assertTrue(clientService.removeClientById(5L));

        //try to delete same client again
        try {
            assertFalse(clientService.removeClientById(5L));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }

        //try to delete client that is in workflow
        try {
            assertFalse(clientService.removeClientById(1L));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }

        //9-1=8
        int countAfter = clientService.getLocalRegistry().size();
        assertEquals(countBefore - 1, countAfter);
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

    /**
     * Testing creating new client
     */
    @Test
    public void createClientTest() {
        System.out.println("_________ START createClientTest _________");

        int countBefore = clientService.getLocalRegistry().size();

        //same companyName
        Client client1 = null;
        try {
            client1 = clientService.createClient(null, "OptTrade", "Bobruysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client1);

        //wrong companyName format
        Client client2 = null;
        try {
            client2 = clientService.createClient(null, "~!cawabanga!~", "Bobruysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client2);

        //wrong city format
        Client client3 = null;
        try {
            client3 = clientService.createClient(null, "Cawabanga", "!~Bobriysk<>", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client3);

        //wrong address format
        Client client4 = null;
        try {
            client4 = clientService.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45!", "Vladimir", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client4);

        //wrong buyerName format
        Client client5 = null;
        try {
            client5 = clientService.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir2", "79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client5);

        //wrong phone format
        Client client6 = null;
        try {
            client6 = clientService.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir", "+79140789975", "vladimir@yandex.ru");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client6);

        //wrong email format
        Client client7 = null;
        try {
            client7 = clientService.createClient(null, "Cawabanga", "Bobriysk", "Lenina 45", "Vladimir", "79140789975", "vladimir@yandex.ru.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client7);

        //creating duplicate client
        Client client8 = null;
        try {
            client8 = clientService.createClient(null, "NinjaRoll", "Tokyo", "Naruto 33", "Naruto Naruto", "71234567889", "naruto@tokyo.jp");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client8);

        //create good new clients
        Client client9 = clientService.createClient(null, "RollCompany", "Monaco", "BigStreet 1", "Paul", "79140782275", "paul@monaco.com");
        assertNotNull(client9);
        Client client10 = clientService.createClient(null, "MyRoll", "Barselona", "Travaha 15", "Antonio", "79122789915", "antonio@travaha.es");
        assertNotNull(client10);

        int countAfter = clientService.getLocalRegistry().size();

        assertEquals(countBefore + 2, countAfter);
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
    }


    /**
     * Testing updating client parameters
     */
    @Test
    public void updateClientTest() {
        System.out.println("_________ START updateClientTest _________");

        int countBefore = clientService.getLocalRegistry().size();

        //try to change client that is not existed
        Client client1 = clientService.findClientById(1L);
        assertNotNull(client1);
        System.out.println(client1);
        client1.setId(100L);
        Client client1AfterUpdate = null;
        try {
            client1AfterUpdate = clientService.updateClient(client1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client1AfterUpdate);

        //try to change client that is in workflow
        Client client2 = clientService.findClientById(7L);
        assertNotNull(client2);
        System.out.println(client2);
        client2.setCity("St.Petersburg");
        Client client2AfterUpdate = null;
        try {
            client2AfterUpdate = clientService.updateClient(client2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client2AfterUpdate);

        //try to change client that has same parameters of another client
        Client client3 = clientService.findClientById(3L);
        assertNotNull(client3);
        System.out.println(client3);
        client3.setId(30L);
        Client client3AfterUpdate = null;
        try {
            client3AfterUpdate = clientService.updateClient(client3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client3AfterUpdate);

        //try to change client with invalid city
        Client client4 = clientService.findClientById(4L);
        assertNotNull(client4);
        System.out.println(client4);
        client4.setCity("798");
        Client client4AfterUpdate = null;
        try {
            client4AfterUpdate = clientService.updateClient(client4);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client4AfterUpdate);

        //try to change client with invalid address
        Client client5 = clientService.findClientById(5L);
        assertNotNull(client5);
        System.out.println(client5);
        client5.setAddress("!~Address");
        Client client5AfterUpdate = null;
        try {
            client5AfterUpdate = clientService.updateClient(client5);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client5AfterUpdate);

        //try to change client with invalid buyerName
        Client client6 = clientService.findClientById(6L);
        assertNotNull(client6);
        System.out.println(client6);
        client6.setBuyerName("Buyer88");
        Client client6AfterUpdate = null;
        try {
            client6AfterUpdate = clientService.updateClient(client6);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client6AfterUpdate);

        //try to change client with invalid buyer phone
        Client client7 = clientService.findClientById(8L);
        assertNotNull(client7);
        System.out.println(client7);
        client7.setPhone("875245698745");
        Client client7AfterUpdate = null;
        try {
            client7AfterUpdate = clientService.updateClient(client7);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client7AfterUpdate);

        //try to change client with invalid buyer email
        Client client8 = clientService.findClientById(9L);
        assertNotNull(client8);
        System.out.println(client8);
        client8.setEmail("mymail!@ggdkm.ru");
        Client client8AfterUpdate = null;
        try {
            client8AfterUpdate = clientService.updateClient(client8);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(client8AfterUpdate);

        //update successfully
        Client client9 = clientService.findClientById(2L);
        assertNotNull(client9);
        System.out.println(client9);
        Client client9Cloned = client9.clone();
        client9Cloned.setEmail("newmail@newdomail.new");
        Client client9AfterUpdate = clientService.updateClient(client9Cloned);
        assertNotNull(client9AfterUpdate);
        System.out.println(client9AfterUpdate);
        assertNotNull(client9AfterUpdate);
        assertEquals(client9Cloned.getEmail(), client9AfterUpdate.getEmail());
        assertNotEquals(client9, client9AfterUpdate);

        int countAfter = clientService.getLocalRegistry().size();
        assertEquals(countBefore, countAfter);
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

}
