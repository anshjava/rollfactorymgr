package ru.kamuzta.rollfactorymgr.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.roll.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
    static AtomicLong count = new AtomicLong(0L);
    static Random random = new SecureRandom();

    public static List<Roll> getRandomRollsList() {
        int pcs = 1 + random.nextInt(9);
        log.info("Create a random list of " + pcs + " rolls...");
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
        Paper paper = (rollType == RollType.DIAMETER) ? getRandomFromArray(Paper.NTC55, Paper.NTC58) : getRandomFromArray(Paper.NTC44, Paper.NTC48);
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
        for (int i = 0; i < 10; i++) {
            sb.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return sb.toString();
    }

    public static String getRandomString(int stringLength) {
        String symbols1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String symbols2 = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(stringLength);
        sb.append(symbols1.charAt(random.nextInt(symbols1.length())));
        for (int i = 1; i < stringLength; i++) {
            sb.append(symbols2.charAt(random.nextInt(symbols2.length())));
        }
        return sb.toString();
    }

    public static String getRandomCity() {
        return getRandomFromArray("New York", "Los Angeles", "Berlin", "Paris", "Moscow", "Tel Aviv");
    }

    public static String getRandomPhone(String firstDigit, int phoneLength) {
        String digits = "0123456789";
        StringBuilder sb = new StringBuilder(phoneLength);
        sb.append(firstDigit);
        for (int i = 1; i < phoneLength; i++) {
            sb.append(digits.charAt(random.nextInt(digits.length())));
        }
        return sb.toString();
    }

    public static String getRandomEmail(int emailLength) {
        //minimum email length is a@b.cd = 7
        if (emailLength < 7) {
            throw new IllegalArgumentException("emailLength is too small");
        }
        String letters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(emailLength)
                .append(StringUtils.repeat(letters.charAt(random.nextInt(letters.length())), emailLength - 6)) //name
                .append("@") //at
                .append(StringUtils.repeat(letters.charAt(random.nextInt(letters.length())), 2)) //domain2
                .append(".") //dot
                .append(StringUtils.repeat(letters.charAt(random.nextInt(letters.length())), 2)); //domain1
        return sb.toString();
    }

    public static String getRandomSKU() {
        String symbols1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String symbols2 = "0123456789";
        int skuLength = 6;
        StringBuilder sb = new StringBuilder(skuLength);
        for (int i = 0; i < skuLength / 2; i++) {
            sb.append(symbols1.charAt(random.nextInt(symbols1.length())));
        }
        for (int i = skuLength / 2; i < skuLength; i++) {
            sb.append(symbols2.charAt(random.nextInt(symbols2.length())));
        }
        return sb.toString();
    }

    public static Client getRandomClient() {
        OffsetDateTime creationDate = OffsetDateTime.now();
        String companyName = getRandomString(5);
        String city = getRandomCity();
        String address = getRandomString(1);
        String buyerName = getRandomString(12);
        String phone = getRandomPhone("7", 11);
        String email = getRandomEmail(15);
        return new Client(count.incrementAndGet(), creationDate, companyName, city, address, buyerName, phone, email);
    }

    private static <T> T getRandomFromList(List<T> sourcelist) {
        Collections.shuffle(sourcelist);
        return sourcelist.get(0);
    }

    @SafeVarargs
    private static <T> T getRandomFromArray(T... sourceItems) {
        return getRandomFromList(Arrays.asList(sourceItems));
    }

}
