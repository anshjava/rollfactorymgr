package ru.kamuzta.rollfactorymgr.model;

import static org.junit.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.utils.RollCalculator;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotSeserializeToJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;
import ru.kamuzta.rollfactorymgr.utils.TestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

@Slf4j
public class RollModelTest {
    private JsonUtil jsonUtil = JsonUtil.getInstance();
    private static RollCalculator rollCalculator;


    @Before
    public void before() {

    }

    /**
     * Testing roll cloning
     */
    @Test
    public void rollGetCloneTest() {
        log.info("_________ START rollGetCloneTest _________");
        Roll roll = TestUtils.getRandomRoll();
        Roll clonedRoll = new Roll(roll);
        assertEquals(roll, clonedRoll);
        assertNotSame(roll, clonedRoll);
    }

    /**
     * Testing roll sort
     */
    @Test
    public void rollCompareTest() {
        log.info("_________ START rollCompareTest _________");
        TreeSet<Roll> rollSet = new TreeSet<>();
        for (int i = 0; i < 100; i++) {
            rollSet.add(TestUtils.getRandomRoll());
        }
        rollSet.forEach(roll -> log.info(roll.toString()));
    }

    /**
     * Testing of roll calculations
     * TODO: check bidirectional calculation of diameter and length
     */
    @Test
    public void rollCalculationTest() {
        log.info("_________ START rollCalculationTest _________");
        Roll roll1 = Roll.builder()
                .id(1L)
                .sku("LEN123")
                .rollType(RollType.LENGTH)
                .paper(Paper.NTC44)
                .widthType(WidthType.WIDTH_57)
                .coreType(CoreType.CORE_12)
                .mainValue(BigDecimal.valueOf(30.0).setScale(1, RoundingMode.HALF_UP))
                .state(RollState.ACTIVE)
                .build();
        assertEquals(BigDecimal.valueOf(30.0).setScale(1, RoundingMode.HALF_UP),
                RollCalculator.calculateLength(roll1));
        assertEquals(BigDecimal.valueOf(45).setScale(0, RoundingMode.HALF_UP),
                RollCalculator.calculateDiameter(roll1));
        assertEquals(BigDecimal.valueOf(0.075).setScale(3, RoundingMode.HALF_UP),
                RollCalculator.calculateWeight(roll1));

        Roll roll2 = Roll.builder()
                .id(2L)
                .sku("DIA123")
                .rollType(RollType.DIAMETER)
                .paper(Paper.NTC44)
                .widthType(WidthType.WIDTH_57)
                .coreType(CoreType.CORE_12)
                .mainValue(BigDecimal.valueOf(45).setScale(0, RoundingMode.HALF_UP))
                .state(RollState.ACTIVE)
                .build();
        assertEquals(BigDecimal.valueOf(30.1).setScale(1, RoundingMode.HALF_UP),
                RollCalculator.calculateLength(roll2));
        assertEquals(BigDecimal.valueOf(45).setScale(0, RoundingMode.HALF_UP),
                RollCalculator.calculateDiameter(roll2));
        assertEquals(BigDecimal.valueOf(0.075).setScale(3, RoundingMode.HALF_UP),
                RollCalculator.calculateWeight(roll2));
    }

    /**
     * Try serialization and deserialization of roll
     */
    @Test
    public void rollSerializationTest() {
        log.info("_________ START rollSerializationTest _________");
        Roll roll1 = Roll.builder()
                .id(1L)
                .sku("LEN123")
                .rollType(RollType.LENGTH)
                .paper(Paper.NTC44)
                .widthType(WidthType.WIDTH_57)
                .coreType(CoreType.CORE_12)
                .mainValue(BigDecimal.valueOf(30.0).setScale(1, RoundingMode.HALF_UP))
                .state(RollState.ACTIVE)
                .build();
        log.info(roll1.toString());
        String json = jsonUtil.writeObject(roll1, CouldNotSeserializeToJsonException::new);
        Roll roll2 = jsonUtil.readValue(json, Roll.class, CouldNotDeserializeJsonException::new);
        log.info(roll2.toString());
        assertEquals(roll1, roll2);
        assertNotSame(roll1, roll2);
    }

    /**
     * Create standard rolls registry
     */
    @Test
    public void rollRegistryCreateTest() {
        log.info("_________ START rollRegistryCreateTest _________");
        List<Roll> rollList = new ArrayList<>();
        rollList.add(Roll.builder().id(1L).sku("LEN5710").mainValue(BigDecimal.valueOf(10.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC48).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.DELETED).build());
        rollList.add(Roll.builder().id(2L).sku("LEN5715").mainValue(BigDecimal.valueOf(15.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC48).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(3L).sku("LEN5717").mainValue(BigDecimal.valueOf(17.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC48).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(4L).sku("LEN5719").mainValue(BigDecimal.valueOf(19.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC48).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(5L).sku("LEN5721").mainValue(BigDecimal.valueOf(21.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC48).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(6L).sku("LEN5723").mainValue(BigDecimal.valueOf(23.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(7L).sku("LEN5725").mainValue(BigDecimal.valueOf(25.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(8L).sku("LEN5727").mainValue(BigDecimal.valueOf(27.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(9L).sku("LEN5729").mainValue(BigDecimal.valueOf(29.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(10L).sku("LEN5730").mainValue(BigDecimal.valueOf(30.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(11L).sku("LEN5735").mainValue(BigDecimal.valueOf(35.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(12L).sku("LEN5740").mainValue(BigDecimal.valueOf(40.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_57).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(13L).sku("LEN8050").mainValue(BigDecimal.valueOf(50.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(14L).sku("LEN8053").mainValue(BigDecimal.valueOf(53.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(15L).sku("LEN8058").mainValue(BigDecimal.valueOf(58.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(16L).sku("LEN8069").mainValue(BigDecimal.valueOf(69.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(17L).sku("LEN8072").mainValue(BigDecimal.valueOf(72.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(18L).sku("LEN8080").mainValue(BigDecimal.valueOf(80.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_12).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(19L).sku("LEN8045").mainValue(BigDecimal.valueOf(45.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_18).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(20L).sku("LEN8061").mainValue(BigDecimal.valueOf(61.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_18).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(21L).sku("LEN8073").mainValue(BigDecimal.valueOf(73.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.LENGTH).paper(Paper.NTC44).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_18).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(22L).sku("DIA8018").mainValue(BigDecimal.valueOf(80.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.DIAMETER).paper(Paper.NTC58).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_18).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(23L).sku("DIA10026").mainValue(BigDecimal.valueOf(100.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.DIAMETER).paper(Paper.NTC58).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_26).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(24L).sku("DIA12026").mainValue(BigDecimal.valueOf(120.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.DIAMETER).paper(Paper.NTC58).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_26).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(25L).sku("DIA15026").mainValue(BigDecimal.valueOf(150.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.DIAMETER).paper(Paper.NTC58).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_26).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(26L).sku("DIA11026").mainValue(BigDecimal.valueOf(110.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.DIAMETER).paper(Paper.NTC55).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_26).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(27L).sku("DIA18026").mainValue(BigDecimal.valueOf(180.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.DIAMETER).paper(Paper.NTC55).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_26).state(RollState.ACTIVE).build());
        rollList.add(Roll.builder().id(28L).sku("DIA20018").mainValue(BigDecimal.valueOf(200.0).setScale(1, RoundingMode.HALF_UP)).rollType(RollType.DIAMETER).paper(Paper.NTC55).widthType(WidthType.WIDTH_80).coreType(CoreType.CORE_18).state(RollState.DELETED).build());

        String json = jsonUtil.writeObject(rollList, CouldNotDeserializeJsonException::new);
        assertNotNull(json);
        log.info(json);
    }

    /**
     * Testing reading jsonRegistry to list
     */
    @Test
    public void rollRegistryReadFromJsonTest() {
        log.info("_________ START rollRegistryReadFromJsonTest _________");

        List<Roll> rollListFromJson = jsonUtil.getListFromJson("rollRegistry.json", Roll.class, CouldNotDeserializeJsonException::new);
        assertNotNull(rollListFromJson);
        assertFalse(rollListFromJson.isEmpty());
        Collections.sort(rollListFromJson);
        rollListFromJson.forEach(roll -> log.info(roll.toString()));
    }


}
