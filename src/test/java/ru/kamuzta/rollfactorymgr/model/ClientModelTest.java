package ru.kamuzta.rollfactorymgr.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.utils.TestUtils;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotSeserializeToJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

@Slf4j
public class ClientModelTest {
    JsonUtil jsonUtil = JsonUtil.getInstance();

    @Before
    public void before() {

    }


    /**
     * Testing client cloning
     */
    @Test
    public void clientGetCloneTest() {
        log.info("_________ START clientGetCloneTest _________");
        Client client = TestUtils.getRandomClient();
        Client clonedClient = new Client(client);
        log.info(client.toString());
        log.info(clonedClient.toString());
        assertEquals(client, clonedClient);
        assertNotSame(client, clonedClient);
    }

    /**
     * Testing client sort
     */
    @Test
    public void clientCompareTest() {
        log.info("_________ START clientCompareTest _________");
        TreeSet<Client> clientSet = new TreeSet<>();
        for (int i = 0; i < 100; i++) {
            clientSet.add(TestUtils.getRandomClient());
        }
        clientSet.forEach(client -> log.info(client.toString()));
    }

    /**
     * Try serialization and deserialization of client
     */
    @Test
    public void clientSerializationTest() {
        log.info("_________ START clientSerializationTest _________");
        Client client1 = Client.builder()
                .id(1L)
                .creationDate(LocalDate.now())
                .companyName("RollTrade Inc.")
                .city("Los Angeles")
                .address("Broadway 57")
                .buyerName("Antonio Banderos")
                .phone("79217770666")
                .email("antonio@rolltrade.com")
                .state(ClientState.ACTIVE)
                .build();
        String json = jsonUtil.writeObject(client1, CouldNotSeserializeToJsonException::new);
        log.info(json);
        Client client2 = jsonUtil.readValue(json, Client.class, CouldNotDeserializeJsonException::new);
        log.info(client1.toString());
        log.info(client2.toString());
        assertEquals(client1, client2);
        assertNotSame(client1, client2);
    }

    /**
     * Create client registry
     */
    @Test
    public void clientRegistryCreateTest() {
        log.info("_________ START clientRegistryCreateTest _________");
        List<Client> clientList = new ArrayList<>();

        clientList.add(Client.builder().id(1L)
                .creationDate(LocalDate.of(1990,2,18))
                .companyName("RollTrade Inc.").city("Los Angeles").address("Broadway 57")
                .buyerName("Antonio Banderos").phone("79217770666").email("antonio@rolltrade.com")
                .state(ClientState.ACTIVE)
                .build());
        clientList.add(Client.builder().id(2L)
                .creationDate(LocalDate.of(1995,1,1))
                .companyName("BestRolls Inc.").city("Los Angeles").address("Broadway 56")
                .buyerName("Nicolas Cage").phone("79256546545").email("nicolas@bestrolls.net")
                .state(ClientState.ACTIVE)
                .build());
        clientList.add(Client.builder().id(3L)
                .creationDate(LocalDate.of(1997,3,5))
                .companyName("MegaRoll Inc.").city("Los Angeles").address("Broadway 55")
                .buyerName("Demy Moore").phone("78521111221").email("dmoore@megaroll.org")
                .state(ClientState.ACTIVE)
                .build());

        clientList.add(Client.builder().id(4L)
                .creationDate(LocalDate.of(2010,1,1))
                .companyName("OptTrade").city("Moscow").address("Lubyanka 12")
                .buyerName("Fedor Bondarchuk").phone("79252256666").email("bondarchuk@mail.ru")
                .state(ClientState.ACTIVE)
                .build());
        clientList.add(Client.builder().id(5L)
                .creationDate(LocalDate.of(2011,2,2))
                .companyName("MarketBest").city("Moscow").address("Red Square 1")
                .buyerName("Dmitry Nagiev").phone("79152365895").email("nagiev@yandex.ru")
                .state(ClientState.ACTIVE)
                .build());
        clientList.add(Client.builder().id(6L)
                .creationDate(LocalDate.of(2012,3,3))
                .companyName("BuySaleInc").city("Moscow").address("Leninsky 43")
                .buyerName("Yury Druz").phone("78546985544").email("druz@rambler.ru")
                .state(ClientState.ACTIVE)
                .build());

        clientList.add(Client.builder().id(7L)
                .creationDate(LocalDate.of(2020,1,1))
                .companyName("NinjaRoll").city("Tokyo").address("Naruto 33")
                .buyerName("Naruto Naruto").phone("78001234565").email("naruto@tokyo.jp")
                .state(ClientState.ACTIVE)
                .build());
        clientList.add(Client.builder().id(8L)
                .creationDate(LocalDate.of(2021,2,2))
                .companyName("ZooNoidos").city("Tokyo").address("Guyver 12")
                .buyerName("Sho Fukumachi").phone("78001234566").email("guyver12@guyver.com")
                .state(ClientState.ACTIVE)
                .build());
        clientList.add(Client.builder().id(9L)
                .creationDate(LocalDate.of(2022,3,3))
                .companyName("OneRoll - OnePunch").city("Tokyo").address("Onepunchman 21")
                .buyerName("Saitama").phone("78001234567").email("one@punch.man")
                .state(ClientState.REMOVED)
                .build());

        String json = jsonUtil.writeObject(clientList, CouldNotDeserializeJsonException::new);
        assertNotNull(json);
        log.info(json);
    }

    /**
     * Testing reading jsonRegistry to list
     */
    @Test
    public void clientRegistryReadFromJsonTest() {
        log.info("_________ START clientRegistryReadFromJsonTest _________");

        List<Client> clientListFromJson = jsonUtil.getListFromJson("clientRegistry.json", Client.class, CouldNotDeserializeJsonException::new);
        assertNotNull(clientListFromJson);
        assertFalse(clientListFromJson.isEmpty());
        Collections.sort(clientListFromJson);
        clientListFromJson.forEach(client -> log.info(client.toString()));
    }

}
