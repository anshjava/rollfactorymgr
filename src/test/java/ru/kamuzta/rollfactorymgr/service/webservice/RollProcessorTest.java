package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.kamuzta.rollfactorymgr.GuiceJUnitRunner;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.modules.EventBusModule;
import ru.kamuzta.rollfactorymgr.processor.RollProcessor;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@Slf4j
@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({EventBusModule.class})
public class RollProcessorTest {

    @Inject
    RollProcessor rollProcessor;

    @Before
    public void before() {
        rollProcessor.updateRegistryFromServer();
    }

    /**
     * Testing obtaining roll registry
     */
    @Test
    public void getRollRegistryTest() {
        log.info("_________ START getRollRegistryTest _________");
        List<Roll> rollRegistry = rollProcessor.getActiveRollsLocal();
        assertNotNull(rollRegistry);
        assertFalse(rollRegistry.isEmpty());
        Collections.sort(rollRegistry);
        rollRegistry.forEach(roll -> {
            log.info(roll.toString());
            assertEquals(RollState.ACTIVE, roll.getState());
        });
    }

    /**
     * Testing find roll by SKU
     */
    @Test
    public void findRollBySkuTest() {
        log.info("_________ START findRollBySkuTest _________");
        Roll roll1 = rollProcessor.findRollBySku("LEN5730");
        assertNotNull(roll1);
        log.info(roll1.toString());

        Roll roll2 = rollProcessor.findRollBySku("DIA12026");
        assertNotNull(roll2);
        log.info(roll2.toString());

        Roll roll3 = null;
        try {
            roll3 = rollProcessor.findRollBySku("GGG123");
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll3);
    }

    /**
     * Testing find roll by id
     */
    @Test
    public void findRollByIdTest() {
        log.info("_________ START findRollByIdTest _________");
        Roll roll1 = rollProcessor.findRollById(4L);
        assertNotNull(roll1);
        log.info(roll1.toString());

        Roll roll2 = rollProcessor.findRollById(5L);
        assertNotNull(roll2);
        log.info(roll2.toString());

        Roll roll3 = null;
        try {
            roll3 = rollProcessor.findRollById(28L);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof WebServiceException);
        }
        assertNull(roll3);
    }

    /**
     * Testing find roll by part of SKU
     */
    @Test
    public void findRollBySkuPatternTest() {
        log.info("_________ START findRollBySkuPatternTest _________");

        List<Roll> result1 = rollProcessor.findRollBySkuPattern("026");
        assertNotNull(result1);
        assertFalse(result1.isEmpty());
        assertEquals(4, result1.size());
        result1.forEach(System.out::println);

        List<Roll> result2 = rollProcessor.findRollBySkuPattern("57");
        assertNotNull(result2);
        assertFalse(result2.isEmpty());
        assertEquals(12, result2.size());
        result2.forEach(System.out::println);

        List<Roll> result3 = rollProcessor.findRollBySkuPattern("56");
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    /**
     * Testing finding roll by collection of params
     */
    @Test
    public void findRollByParamsTest() {
        log.info("_________ START findRollByParamsTest _________");

        List<Roll> result1 = rollProcessor.findRollByParams(null, null, RollType.LENGTH, Paper.NTC48, null, null, null);
        assertNotNull(result1);
        log.info("Result1:");
        result1.forEach(System.out::println);
        assertEquals(5, result1.size());

        List<Roll> result2 = rollProcessor.findRollByParams(null, null, RollType.DIAMETER, Paper.NTC55, null, null, null);
        assertNotNull(result2);
        log.info("Result2:");
        result2.forEach(System.out::println);
        assertEquals(1, result2.size());

        List<Roll> result3 = rollProcessor.findRollByParams(null, null, null, Paper.NTC44, null, null, BigDecimal.valueOf(80));
        assertNotNull(result3);
        log.info("Result3:");
        result3.forEach(System.out::println);
        assertEquals(1, result3.size());

        List<Roll> result4 = rollProcessor.findRollByParams(null, null, RollType.DIAMETER, null, WidthType.WIDTH_57, null, null);
        assertNotNull(result4);
        log.info("Result4:");
        result4.forEach(System.out::println);
        assertTrue(result4.isEmpty());

        //find by part of sku
        List<Roll> result5 = rollProcessor.findRollByParams(null, "LEN", null, null, WidthType.WIDTH_57, null, null);
        assertNotNull(result5);
        log.info("Result5:");
        result5.forEach(System.out::println);
        assertEquals(12, result5.size());

        //find by part of id
        List<Roll> result6 = rollProcessor.findRollByParams(2L, null, null, null, null, null, null);
        assertNotNull(result6);
        log.info("Result6:");
        result6.forEach(System.out::println);
        assertEquals(9, result6.size());
    }

    /**
     * Testing removing roll by SKU
     */
    @Test
    public void removeRollBySkuTest() throws ValidationException {
        log.info("_________ START removeRollBySkuTest _________");

        int countBefore = rollProcessor.getActiveRollsLocal().size();
        assertEquals(26, countBefore);

        assertTrue(rollProcessor.removeRollBySku("LEN8050"));

        //try to delete same roll again
        try {
            assertFalse(rollProcessor.removeRollBySku("LEN8050"));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }

        //try to delete roll that is in workflow
        try {
            assertFalse(rollProcessor.removeRollBySku("LEN5710"));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        Roll rollInWorkflow = rollProcessor.findRollBySku("LEN5710");
        assertNotNull(rollInWorkflow);

        //26-1=25
        int countAfter = rollProcessor.getActiveRollsLocal().size();
        assertEquals(25, countAfter);
        assertEquals(countBefore - 1, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

    /**
     * Testing creating new roll
     */
    @Test
    public void createRollTest() throws ValidationException {
        log.info("_________ START createRollTest _________");

        int countBefore = rollProcessor.getActiveRollsLocal().size();

        //same SKU
        Roll roll1 = null;
        try {
            roll1 = rollProcessor.createRoll("LEN5730", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_18, BigDecimal.valueOf(50.0));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll1);

        //SKU is uniq, bus equal params
        Roll roll2 = null;
        try {
            roll2 = rollProcessor.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(30.0));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll2);

        //cant create diam roll with diam = core
        Roll roll3 = null;
        try {
            roll3 = rollProcessor.createRoll("UNIQUE", RollType.DIAMETER, Paper.NTC58, WidthType.WIDTH_80, CoreType.CORE_12, BigDecimal.valueOf(12.0));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll3);

        //cant create diam roll with diam < core
        Roll roll4 = null;
        try {
            roll4 = rollProcessor.createRoll("UNIQUE", RollType.DIAMETER, Paper.NTC58, WidthType.WIDTH_80, CoreType.CORE_26, BigDecimal.valueOf(20.0));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll4);

        //cant create length roll with length < 0
        Roll roll5 = null;
        try {
            roll5 = rollProcessor.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(-30.0));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll5);

        //cant create length roll with lenrth = 0
        Roll roll6 = null;
        try {
            roll6 = rollProcessor.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(0.0));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll6);

        //cant create roll with sku with wrong symbols
        Roll roll7 = null;
        try {
            roll7 = rollProcessor.createRoll("Roll&", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(28.0));
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll7);


        Roll roll8 = rollProcessor.createRoll("UNIQUE", RollType.LENGTH, Paper.NTC44, WidthType.WIDTH_57, CoreType.CORE_12, BigDecimal.valueOf(31.0));
        assertNotNull(roll8);
        Roll roll9 = rollProcessor.createRoll("UNIQUE2", RollType.DIAMETER, Paper.NTC55, WidthType.WIDTH_80, CoreType.CORE_18, BigDecimal.valueOf(90.0));
        assertNotNull(roll9);

        int countAfter = rollProcessor.getActiveRollsLocal().size();
        assertEquals(countBefore + 2, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

    /**
     * Testing updating roll parameters
     */
    @Test
    public void updateRollTest() throws ValidationException {
        log.info("_________ START updateRollTest _________");

        int countBefore = rollProcessor.getActiveRollsLocal().size();

        //try to change roll type with not valid params
        Roll roll1 = rollProcessor.findRollBySku("LEN5725");
        assertNotNull(roll1);
        log.info(roll1.toString());
        Roll roll1Cloned = new Roll(roll1);
        roll1Cloned.setRollType(RollType.DIAMETER);
        roll1Cloned.setCoreType(CoreType.CORE_26);
        Roll roll1AfterUpdate = null;
        try {
            roll1AfterUpdate = rollProcessor.updateRoll(roll1Cloned);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll1AfterUpdate);
        roll1AfterUpdate = rollProcessor.findRollBySku(roll1.getSku());
        assertNotEquals(roll1Cloned, roll1AfterUpdate);

        //increase length, but roll with same length exists
        Roll roll2 = rollProcessor.findRollBySku("LEN5725");
        assertNotNull(roll2);
        log.info(roll2.toString());
        Roll roll2Cloned = new Roll(roll2);
        roll2Cloned.setMainValue(BigDecimal.valueOf(27.0));
        Roll roll2AfterUpdate = null;
        try {
            roll2AfterUpdate = rollProcessor.updateRoll(roll2Cloned);
        } catch (Exception e) {
            log.warn(e.getMessage());
            assertTrue(e instanceof ValidationException);
        }
        assertNull(roll2AfterUpdate);
        roll2AfterUpdate = rollProcessor.findRollBySku(roll2.getSku());
        assertNotEquals(roll2Cloned, roll2AfterUpdate);

        //decrease length - success
        Roll roll3 = rollProcessor.findRollBySku("LEN5725");
        assertNotNull(roll3);
        log.info(roll3.toString());
        Roll roll3Cloned = new Roll(roll3);
        roll3Cloned.setMainValue(BigDecimal.valueOf(24.0));
        Roll roll3AfterUpdate = rollProcessor.updateRoll(roll3Cloned);
        assertNotNull(roll3AfterUpdate);
        assertEquals(roll3Cloned, roll3AfterUpdate);
        assertNotEquals(roll3, roll3AfterUpdate);

        int countAfter = rollProcessor.getActiveRollsLocal().size();
        assertEquals(countBefore, countAfter);
        log.info("countBefore: " + countBefore + " countAfter: " + countAfter);
    }

}
