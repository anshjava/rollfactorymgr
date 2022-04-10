package ru.kamuzta.rollfactorymgr.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;
import ru.kamuzta.rollfactorymgr.model.order.Order;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {
    static AtomicLong count = new AtomicLong(0L);
    @Getter
    static Random random = new SecureRandom();
    static JsonUtil jsonUtil = JsonUtil.getInstance();

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
        RollType rollType = getRandomElementFromArray(RollType.values());
        Paper paper = (rollType == RollType.DIAMETER) ? getRandomElementFromArray(Paper.NTC55, Paper.NTC58) : getRandomElementFromArray(Paper.NTC44, Paper.NTC48);
        WidthType widthType = (rollType == RollType.DIAMETER) ? WidthType.WIDTH_80 : getRandomElementFromArray(WidthType.values());
        CoreType coreType = (widthType == WidthType.WIDTH_57) ? CoreType.CORE_12 : getRandomElementFromArray(CoreType.CORE_18, CoreType.CORE_26);
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

        return new Roll(count.incrementAndGet(), sku, rollType, paper, widthType, coreType, value, RollState.ACTIVE);
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
        return getRandomElementFromArray("New York", "Los Angeles", "Berlin", "Paris", "Moscow", "Tel Aviv");
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
        return new Client(count.incrementAndGet(), creationDate, companyName, city, address, buyerName, phone, email, ClientState.ACTIVE);
    }

    public static OrderLine getRandomOrderLine() {
        Integer quantity = random.nextInt(1000);
        Roll roll = getRandomStandardRoll();
        return new OrderLine(count.incrementAndGet(), roll, quantity, OrderState.NEW);
    }

    public static List<OrderLine> getRandomOrderLineList(int count) {
        List<OrderLine> orderLines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            orderLines.add(getRandomOrderLine());
        }
        return orderLines;
    }

    public static Order getRandomOrder() {
        Integer quantity = random.nextInt(20);
        List<OrderLine> orderLines = getRandomOrderLineList(quantity);
        return new Order(count.incrementAndGet(), OffsetDateTime.now(), getRandomClient(), orderLines, OrderState.NEW);
    }

    public static Roll getRandomStandardRoll() {
        List<Roll> standartRollList = jsonUtil.getListFromJson("rollRegistry.json", Roll.class, CouldNotDeserializeJsonException::new);
        return getRandomElementFromList(standartRollList);
    }

    public static Roll getStandardRoll(Long id) {
        List<Roll> standartRollList = jsonUtil.getListFromJson("rollRegistry.json", Roll.class, CouldNotDeserializeJsonException::new);
        return standartRollList.stream().filter(roll -> roll.getId().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("There is no standart roll with id " + id));
    }

    public static <T> List<T> getRandomElementsFromList(List<T> sourcelist, int count) {
        Collections.shuffle(sourcelist);
        return sourcelist.stream().limit(Math.min(count, sourcelist.size())).collect(Collectors.toList());
    }

    public static <T> T getRandomElementFromList(List<T> sourcelist) {
        Collections.shuffle(sourcelist);
        return sourcelist.get(0);
    }

    @SafeVarargs
    public static <T> T getRandomElementFromArray(T... sourceItems) {
        return getRandomElementFromList(Arrays.asList(sourceItems));
    }

}
