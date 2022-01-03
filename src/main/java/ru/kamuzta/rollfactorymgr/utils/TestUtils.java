package ru.kamuzta.rollfactorymgr.utils;

import lombok.extern.slf4j.Slf4j;
import ru.kamuzta.rollfactorymgr.model.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TestUtils {
    static AtomicLong count = new AtomicLong(0L);
    static Random random = new SecureRandom();

    public static List<Roll> getRandomRollsList() {
        int pcs = random.nextInt(10);
        log.info("Create a random list of " + pcs +" rolls...");
        List<Roll> rolls = new ArrayList<>();
        for (int i = 0; i < pcs; i++) {
            rolls.add(getRandomRoll());
        }
        Collections.sort(rolls);
        return rolls;
    }
    public static Roll getRandomRoll() {
        String sku = getRandomSKU();
        RollType rollType = getRandomFromArray(RollType.values());
        Paper paper = (rollType == RollType.DIAMETER) ? getRandomFromArray(Paper.NTC55,Paper.NTC58) : getRandomFromArray(Paper.NTC44,Paper.NTC48);
        WidthType widthType = (rollType == RollType.DIAMETER) ? WidthType.WIDTH_80 : getRandomFromArray(WidthType.values());
        CoreType coreType = (widthType == WidthType.WIDTH_57) ? CoreType.CORE_12 : getRandomFromArray(CoreType.CORE_18, CoreType.CORE_26);
        BigDecimal value;

        switch (rollType) {
            case LENGTH:
                value = widthType == WidthType.WIDTH_57
                        ? BigDecimal.valueOf(10.0f + random.nextInt(80))
                        : BigDecimal.valueOf(30.0f + random.nextInt(200));
                break;
            case DIAMETER:
                value = widthType == WidthType.WIDTH_57
                        ? coreType.getDiameter().add(BigDecimal.valueOf(10.0f + random.nextInt(80)))
                        : coreType.getDiameter().add(BigDecimal.valueOf(10.0f + random.nextInt(200)));
                break;
            default:
                throw new IllegalArgumentException("Wrong RollType");
        }

        return new Roll(count.incrementAndGet(), sku, rollType, paper, widthType, coreType, value);
    }

    private static String getRandomSn() {
        String symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(symbols.charAt(random.nextInt(symbols.length())));
        return sb.toString();
    }

    public static String getRandomName() {
        String symbols1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String symbols2 = "abcdefghijklmnopqrstuvwxyz";
        int nameLength = 7 + random.nextInt(3);
        StringBuilder sb = new StringBuilder(nameLength);
        sb.append(symbols1.charAt(random.nextInt(symbols1.length())));
        for (int i = 1; i < nameLength; i++)
            sb.append(symbols2.charAt(random.nextInt(symbols2.length())));
        return sb.toString();
    }

    public static String getRandomSKU() {
        String symbols1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String symbols2 = "0123456789";
        int skuLength = 6;
        StringBuilder sb = new StringBuilder(skuLength);
        for (int i = 0; i < skuLength/2; i++) {
            sb.append(symbols1.charAt(random.nextInt(symbols1.length())));
        }
        for (int i = skuLength/2; i < skuLength; i++) {
            sb.append(symbols2.charAt(random.nextInt(symbols2.length())));
        }
        return sb.toString();
    }

    private static <T> T getRandomFromList(List<T> sourcelist) {
        Collections.shuffle(sourcelist);
        return sourcelist.get(0);
    }
    @SafeVarargs
    private static <T> T getRandomFromArray(T...sourceItems) {
        return getRandomFromList(Arrays.asList(sourceItems));
    }

}
