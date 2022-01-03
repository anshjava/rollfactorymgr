package ru.kamuzta.rollfactorymgr.service;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.kamuzta.rollfactorymgr.GuiceJUnitRunner;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.*;
import ru.kamuzta.rollfactorymgr.modules.EventBusModule;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@Slf4j
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({EventBusModule.class})
public class RollServiceTest {

    @Inject
    RollService rollService;

    @Before
    public void before() {
        rollService.updateRegistryFromServer();
    }

    @Test
    /**
     * Testing obtaining roll registry
     */
    public void getRollRegistryTest() {
        System.out.println("_________ START getRollRegistryTest _________");
        List<Roll> rollRegistry = rollService.getLocalRollRegistry();
        assertNotNull(rollRegistry);
        assertFalse(rollRegistry.isEmpty());
        Collections.sort(rollRegistry);
        rollRegistry.forEach(System.out::println);
    }

    @Test
    /**
     * Testing find roll by SKU
     */
    public void findRollBySkuTest() {
        System.out.println("_________ START findRollBySkuTest _________");
        Roll roll1 = rollService.findRollBySku("LEN5730");
        assertNotNull(roll1);
        System.out.println(roll1);

        Roll roll2 = rollService.findRollBySku("DIA12026");
        assertNotNull(roll2);
        System.out.println(roll2);

        Roll roll3 = null;
        try {
            roll3 = rollService.findRollBySku("GGG123");
        } catch (Exception e) {
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll3);
    }

    @Test
    /**
     * Testing find roll by part of SKU
     */
    public void findRollBySkuPatternTest() {
        System.out.println("_________ START findRollBySkuPatternTest _________");

        List<Roll> result1 = rollService.findRollBySkuPattern("LEN80");
        assertNotNull(result1);
        assertFalse(result1.isEmpty());
        result1.forEach(System.out::println);

        List<Roll> result2 = rollService.findRollBySkuPattern("57");
        assertNotNull(result2);
        assertFalse(result2.isEmpty());
        result2.forEach(System.out::println);

        List<Roll> result3 = rollService.findRollBySkuPattern("56");
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    @Test
    /**
     * Testing finding roll by collection of params
     */
    public void findRollByParamsTest() {
        System.out.println("_________ START findRollByParamsTest _________");

        List<Roll> result1 = rollService.findRollByParams(RollType.LENGTH,Paper.NTC48,null,null,null);
        assertNotNull(result1);
        assertEquals(5, result1.size());
        result1.forEach(System.out::println);

        List<Roll> result2 = rollService.findRollByParams(RollType.DIAMETER,null,null,null,null);
        assertNotNull(result2);
        assertEquals(7, result2.size());
        result2.forEach(System.out::println);

        List<Roll> result3 = rollService.findRollByParams(null,null,null,null, BigDecimal.valueOf(80));
        assertNotNull(result3);
        assertEquals(2, result3.size());
        result3.forEach(System.out::println);

        List<Roll> result4 = rollService.findRollByParams(RollType.DIAMETER,null,WidthType.WIDTH_57,null, null);
        assertNotNull(result4);
        assertEquals(0, result4.size());
    }

    @Test
    /**
     * Testing removing roll by SKU
     */
    public void removeRollBySkuTest() {
        System.out.println("_________ START removeRollBySkuTest _________");

        int countBefore = rollService.getLocalRollRegistry().size();
        assertTrue(rollService.removeRollBySku("LEN5729"));

        try {
            rollService.removeRollBySku("LEN5729");
        } catch (Exception e) {
            assertTrue(e instanceof WebServiceException);
        }

        int countAfter = rollService.getLocalRollRegistry().size();
        assertNotEquals(countBefore,countAfter);
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

    @Test
    /**
     * Testing creating new roll
     */
    public void createRollTest() {
        System.out.println("_________ START createRollTest _________");

        int countBefore = rollService.getLocalRollRegistry().size();

        //same SKU
        Roll roll1 = null;
        try {
            roll1 = rollService.createRoll("LEN5730", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_18, BigDecimal.valueOf(50.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll1);

        //SKU is uniq, bus equal params
        Roll roll2 = null;
        try {
            roll2 = rollService.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(30.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll2);

        //cant create diam roll with diam = core
        Roll roll3 = null;
        try {
            roll3 = rollService.createRoll("UNIQUE", RollType.DIAMETER, Paper.NTC58, WidthType.WIDTH_80, CoreType.CORE_12, BigDecimal.valueOf(12.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll3);

        //cant create diam roll with diam < core
        Roll roll4 = null;
        try {
            roll4 = rollService.createRoll("UNIQUE", RollType.DIAMETER, Paper.NTC58, WidthType.WIDTH_80, CoreType.CORE_26, BigDecimal.valueOf(20.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll4);

        //cant create length roll with lenrth < 0
        Roll roll5 = null;
        try {
            roll5 = rollService.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(-30.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll5);

        //cant create length roll with lenrth = 0
        Roll roll6 = null;
        try {
            roll6 = rollService.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(0.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll6);

        //cant create roll with sku with wrong symbols
        Roll roll7 = null;
        try {
            roll7 = rollService.createRoll("Ролик1", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(28.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll7);


        Roll roll8 = rollService.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(31.0));
        assertNotNull(roll8);
        Roll roll9 = rollService.createRoll("UNIQUE2", RollType.DIAMETER, Paper.NTC55, WidthType.WIDTH_80, CoreType.CORE_18, BigDecimal.valueOf(90.0));
        assertNotNull(roll9);

        rollService.updateRegistryFromServer();
        int countAfter = rollService.getLocalRollRegistry().size();
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
        assertNotEquals(countBefore,countAfter);
    }

    @Test
    /**
     * Testing updating roll parameters
     */
    public void updateRollTest() {
        System.out.println("_________ START updateRollTest _________");

        int countBefore = rollService.getLocalRollRegistry().size();

        //try to change roll type with not valid params
        Roll roll1 = rollService.findRollBySku("LEN5717");
        assertNotNull(roll1);
        System.out.println(roll1);
        roll1.setRollType(RollType.DIAMETER);
        roll1.setCoreType(CoreType.CORE_18);
        Roll roll1AfterUpdate = null;
        try {
            roll1AfterUpdate = rollService.updateRoll(roll1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll1AfterUpdate);
        roll1AfterUpdate = rollService.findRollBySku("LEN5717");
        assertNotEquals(roll1, roll1AfterUpdate);

        //increase length, but roll with same length exists
        Roll roll2 = rollService.findRollBySku("LEN5717");
        assertNotNull(roll2);
        System.out.println(roll2);
        roll2.setValue(BigDecimal.valueOf(19.0));
        Roll roll2AfterUpdate = null;
        try {
            roll2AfterUpdate = rollService.updateRoll(roll2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll2AfterUpdate);
        roll2AfterUpdate = rollService.findRollBySku("LEN5717");
        assertNotEquals(roll2, roll2AfterUpdate);

        //decrease length - success
        Roll roll3 = rollService.findRollBySku("LEN5717");
        assertNotNull(roll3);
        System.out.println(roll3);
        Roll roll3Cloned = roll3.clone();
        roll3Cloned.setValue(BigDecimal.valueOf(12.0));
        Roll roll3AfterUpdate = rollService.updateRoll(roll3Cloned);
        assertNotNull(roll3AfterUpdate);
        assertEquals(roll3Cloned.getSku(),roll3AfterUpdate.getSku());
        assertNotEquals(roll3,roll3AfterUpdate);

        int countAfter = rollService.getLocalRollRegistry().size();
        assertEquals(countBefore,countAfter);
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

}
