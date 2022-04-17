package ru.kamuzta.rollfactorymgr.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.model.roll.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RollCalculator {

    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);

    public static BigDecimal calculateLength(@NotNull Roll roll) {
        return calculateLength(roll.getRollType(), roll.getPaper(), roll.getCoreType(), roll.getMainValue());
    }

    public static BigDecimal calculateLength(@NotNull RollType rollType, @NotNull Paper paper, @NotNull CoreType coreType, @NotNull BigDecimal mainValue) {
        switch (rollType) {
            case LENGTH:
                return mainValue.setScale(1, RoundingMode.HALF_UP);
            case DIAMETER:
                return mainValue.setScale(3, RoundingMode.HALF_UP).pow(2)
                        .subtract(coreType.getDiameter().pow(2))
                        .divide(paper.getThickness().multiply(BigDecimal.valueOf(4)), RoundingMode.HALF_UP)
                        .multiply(PI)
                        .setScale(1, RoundingMode.HALF_UP);
            default:
                throw new IllegalArgumentException("Wrong rollType");
        }
    }

    public static BigDecimal calculateDiameter(@NotNull Roll roll) {
        return calculateDiameter(roll.getRollType(), roll.getPaper(), roll.getCoreType(), roll.getMainValue());
    }

    public static BigDecimal calculateDiameter(@NotNull RollType rollType, @NotNull Paper paper, @NotNull CoreType coreType, @NotNull BigDecimal mainValue) {
        switch (rollType) {
            case LENGTH:
                return sqrt(BigDecimal.valueOf(4).setScale(3, RoundingMode.HALF_UP)
                        .multiply(paper.getThickness())
                        .multiply(mainValue)
                        .divide(PI, RoundingMode.HALF_UP)
                        .add(coreType.getDiameter().pow(2))
                ).setScale(0, RoundingMode.HALF_UP);
            case DIAMETER:
                return mainValue.setScale(0, RoundingMode.HALF_UP);
            default:
                throw new IllegalArgumentException("Wrong rollType");
        }
    }

    public static BigDecimal calculateWeight(@NotNull Roll roll) {
        return calculateWeight(roll.getRollType(), roll.getPaper(), roll.getWidthType(), roll.getCoreType(), roll.getMainValue());
    }

    public static BigDecimal calculateWeight(@NotNull RollType rollType, @NotNull Paper paper, @NotNull WidthType widthType, @NotNull CoreType coreType, @NotNull BigDecimal mainValue) {
        return widthType.getWidth().setScale(5, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP)
                .multiply(calculateLength(rollType, paper, coreType, mainValue))
                .multiply(paper.getWeight())
                .divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP)
                .setScale(3, RoundingMode.HALF_UP);
    }

    //starting from jdk9 BigDecimal has its own sqrt
    private static BigDecimal sqrt(BigDecimal value) {
        return BigDecimal.valueOf(Math.sqrt(value.doubleValue()));
    }
}
