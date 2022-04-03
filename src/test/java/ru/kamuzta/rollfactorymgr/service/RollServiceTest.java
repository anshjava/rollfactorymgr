package ru.kamuzta.rollfactorymgr.service;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.kamuzta.rollfactorymgr.GuiceJUnitRunner;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.modules.EventBusModule;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@Slf4j
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({EventBusModule.class})
public class RollServiceTest {

    JsonUtil jsonUtil = JsonUtil.getInstance();

    @Inject
    RollService rollService;

    @Before
    public void before() {
        rollService.updateRegistryFromServer();
        //setup test roll registry before each test
        rollService.getLocalRegistry().stream().filter(roll -> !roll.getSku().equals("LEN5710")).forEach(roll -> rollService.removeRollBySku(roll.getSku()));
        List<Roll> testRolls = jsonUtil.getListFromJson("rollRegistry.json", Roll.class, CouldNotDeserializeJsonException::new);
        testRolls.stream().filter(roll -> !roll.getSku().equals("LEN5710")).forEach(roll -> rollService.createRoll(
                roll.getSku(),
                roll.getRollType(),
                roll.getPaper(),
                roll.getWidthType(),
                roll.getCoreType(),
                roll.getMainValue()));
    }

    /**
     * Testing obtaining roll registry
     */
    @Test
    public void getRollRegistryTest() {
        System.out.println("_________ START getRollRegistryTest _________");
        List<Roll> rollRegistry = rollService.getLocalRegistry();
        assertNotNull(rollRegistry);
        assertFalse(rollRegistry.isEmpty());
        Collections.sort(rollRegistry);
        rollRegistry.forEach(System.out::println);
    }

    /**
     * Testing find roll by SKU
     */
    @Test
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
            System.out.println(e.getMessage());
        }
        assertNull(roll3);
    }

    /**
     * Testing find roll by id
     */
    @Test
    public void findRollByIdTest() {
        System.out.println("_________ START findRollByIdTest _________");
        Roll roll1 = rollService.findRollById(4L);
        assertNotNull(roll1);
        System.out.println(roll1);

        Roll roll2 = rollService.findRollById(5L);
        assertNotNull(roll2);
        System.out.println(roll2);

        Roll roll3 = null;
        try {
            roll3 = rollService.findRollById(100L);
        } catch (Exception e) {
            assertTrue(e instanceof WebServiceException);
            System.out.println(e.getMessage());
        }
        assertNull(roll3);
    }

    /**
     * Testing find roll by part of SKU
     */
    @Test
    public void findRollBySkuPatternTest() {
        System.out.println("_________ START findRollBySkuPatternTest _________");

        List<Roll> result1 = rollService.findRollBySkuPattern("LEN80");
        assertNotNull(result1);
        assertFalse(result1.isEmpty());
        assertEquals(9, result1.size());
        result1.forEach(System.out::println);

        List<Roll> result2 = rollService.findRollBySkuPattern("57");
        assertNotNull(result2);
        assertFalse(result2.isEmpty());
        assertEquals(12, result2.size());
        result2.forEach(System.out::println);

        List<Roll> result3 = rollService.findRollBySkuPattern("56");
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    /**
     * Testing finding roll by collection of params
     */
    @Test
    public void findRollByParamsTest() {
        System.out.println("_________ START findRollByParamsTest _________");

        List<Roll> result1 = rollService.findRollByParams(null, null, RollType.LENGTH, Paper.NTC48, null, null, null);
        assertNotNull(result1);
        assertEquals(5, result1.size());
        System.out.println("Result1:");
        result1.forEach(System.out::println);

        List<Roll> result2 = rollService.findRollByParams(null, null, RollType.DIAMETER, Paper.NTC55, null, null, null);
        assertNotNull(result2);
        assertEquals(3, result2.size());
        System.out.println("Result2:");
        result2.forEach(System.out::println);

        List<Roll> result3 = rollService.findRollByParams(null, null, null, Paper.NTC44, null, null, BigDecimal.valueOf(80));
        assertNotNull(result3);
        assertEquals(1, result3.size());
        System.out.println("Result3:");
        result3.forEach(System.out::println);

        List<Roll> result4 = rollService.findRollByParams(null, null, RollType.DIAMETER, null, WidthType.WIDTH_57, null, null);
        assertNotNull(result4);
        assertTrue(result4.isEmpty());
        System.out.println("Result4:");
        result4.forEach(System.out::println);

        //find by part of sku
        List<Roll> result5 = rollService.findRollByParams(null, "LEN", null, null, WidthType.WIDTH_57, null, null);
        assertNotNull(result5);
        assertEquals(12, result5.size());
        System.out.println("Result5:");
        result5.forEach(System.out::println);

        //find by part of id
        List<Roll> result6 = rollService.findRollByParams(2L, null, null, null, null, null, null);
        assertNotNull(result6);
        assertEquals(11, result6.size());
        System.out.println("Result6:");
        result6.forEach(System.out::println);
    }

    /**
     * Testing removing roll by SKU
     */
    @Test
    public void removeRollBySkuTest() {
        System.out.println("_________ START removeRollBySkuTest _________");

        int countBefore = rollService.getLocalRegistry().size();
        assertTrue(rollService.removeRollBySku("DIA12026"));

        //try to delete same roll again
        try {
            assertFalse(rollService.removeRollBySku("DIA12026"));
        } catch (Exception e) {
            assertTrue(e instanceof WebServiceException);
        }

        //try to delete roll that is in workflow
        try {
            assertFalse(rollService.removeRollBySku("LEN5710"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        Roll rollInWorkflow = rollService.findRollBySku("LEN5710");
        assertNotNull(rollInWorkflow);

        //28-1=27
        int countAfter = rollService.getLocalRegistry().size();
        assertEquals(countBefore - 1, countAfter);
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

    /**
     * Testing creating new roll
     */
    @Test
    public void createRollTest() {
        System.out.println("_________ START createRollTest _________");

        int countBefore = rollService.getLocalRegistry().size();

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

        //cant create length roll with length < 0
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
            roll7 = rollService.createRoll("Roll&", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(28.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll7);


        Roll roll8 = rollService.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(31.0));
        assertNotNull(roll8);
        Roll roll9 = rollService.createRoll("UNIQUE2", RollType.DIAMETER, Paper.NTC55, WidthType.WIDTH_80, CoreType.CORE_18, BigDecimal.valueOf(90.0));
        assertNotNull(roll9);

        int countAfter = rollService.getLocalRegistry().size();
        assertEquals(countBefore + 2, countAfter);
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

    /**
     * Testing updating roll parameters
     */
    @Test
    public void updateRollTest() {
        System.out.println("_________ START updateRollTest _________");

        int countBefore = rollService.getLocalRegistry().size();

        //try to change roll type with not valid params
        Roll roll1 = rollService.findRollBySku("LEN5717");
        assertNotNull(roll1);
        System.out.println(roll1);
        Roll roll1Cloned = new Roll(roll1);
        roll1Cloned.setRollType(RollType.DIAMETER);
        roll1Cloned.setCoreType(CoreType.CORE_18);
        Roll roll1AfterUpdate = null;
        try {
            roll1AfterUpdate = rollService.updateRoll(roll1Cloned);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll1AfterUpdate);
        roll1AfterUpdate = rollService.findRollBySku(roll1.getSku());
        assertNotEquals(roll1Cloned, roll1AfterUpdate);

        //increase length, but roll with same length exists
        Roll roll2 = rollService.findRollBySku("LEN5717");
        assertNotNull(roll2);
        System.out.println(roll2);
        Roll roll2Cloned = new Roll(roll2);
        roll2Cloned.setMainValue(BigDecimal.valueOf(19.0));
        Roll roll2AfterUpdate = null;
        try {
            roll2AfterUpdate = rollService.updateRoll(roll2Cloned);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll2AfterUpdate);
        roll2AfterUpdate = rollService.findRollBySku(roll2.getSku());
        assertNotEquals(roll2Cloned, roll2AfterUpdate);

        //decrease length - success
        Roll roll3 = rollService.findRollBySku("LEN5717");
        assertNotNull(roll3);
        System.out.println(roll3);
        Roll roll3Cloned = new Roll(roll3);
        roll3Cloned.setMainValue(BigDecimal.valueOf(12.0));
        Roll roll3AfterUpdate = rollService.updateRoll(roll3Cloned);
        assertNotNull(roll3AfterUpdate);
        assertEquals(roll3Cloned, roll3AfterUpdate);
        assertNotEquals(roll3, roll3AfterUpdate);

        int countAfter = rollService.getLocalRegistry().size();
        assertEquals(countBefore, countAfter);
        System.out.println("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

}
