package ru.kamuzta.rollfactorymgr.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonPropertyOrder({"id", "sku", "rollType", "paper", "widthType", "coreType", "mainValue"})
public class Roll implements Comparable<Roll> {
    private static BigDecimal PI = BigDecimal.valueOf(Math.PI);
    @NotNull
    private Long id;
    @NotNull
    private String sku;
    @NotNull
    private RollType rollType;
    @NotNull
    private Paper paper;
    @NotNull
    private WidthType widthType;
    @NotNull
    private CoreType coreType;
    @NotNull
    private BigDecimal mainValue;

    @Override
    public Roll clone() {
        return new Roll(this.id, this.sku, this.rollType, this.paper, this.widthType, this.coreType, this.mainValue);
    }

    public BigDecimal calculateLength() {
        switch (rollType) {
            case LENGTH:
                return mainValue.setScale(1, RoundingMode.HALF_UP);
            case DIAMETER:
                return mainValue.setScale(3,RoundingMode.HALF_UP).pow(2)
                        .subtract(coreType.getDiameter().pow(2))
                        .divide(paper.getThickness().multiply(BigDecimal.valueOf(4)), RoundingMode.HALF_UP)
                        .multiply(PI)
                        .setScale(1, RoundingMode.HALF_UP);
            default:
                throw new IllegalArgumentException("Wrong rollType");
        }
    }

    public BigDecimal calculateDiameter() {
        switch (rollType) {
            case LENGTH:
                return sqrt(BigDecimal.valueOf(4).setScale(3,RoundingMode.HALF_UP)
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

    public BigDecimal calculateWeight() {
        return widthType.getWidth().setScale(5,RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP)
                .multiply(calculateLength())
                .multiply(paper.getWeight())
                .divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP)
                .setScale(3, RoundingMode.HALF_UP);
    }

    //starting by jdk9 BigDecimal has its own sqrt
    private BigDecimal sqrt(BigDecimal value) {
        return BigDecimal.valueOf(Math.sqrt(value.doubleValue()));
    }

    @Override
    public String toString() {
        return String.format("Roll: [%d] [%s] %s %s %.0f x %.1f%s x %.0f %.0fg/m2",
                id,
                sku,
                rollType,
                paper,
                widthType.getWidth(),
                rollType == RollType.LENGTH ? calculateLength() : calculateDiameter(),
                rollType == RollType.LENGTH ? "M" : "mm",
                coreType.getDiameter(),
                getPaper().getWeight());
    }

    //sort Roll by:
    // rollType (LENGTH first)
    // paper (low-weight first)
    // width (low-width first)
    // core (smaller first)
    // value (natural)
    // SKU (natural)
    @Override
    public int compareTo(@NotNull Roll that) {
        int result = 0;

        if (this.equals(that)) {
            return result;
        } else if ((result = this.rollType.compareTo(that.getRollType())) == 0) {
            if ((result = this.paper.compareTo(that.getPaper())) == 0) {
                if ((result = this.widthType.compareTo(that.getWidthType())) == 0) {
                    if ((result = this.coreType.compareTo(that.getCoreType())) == 0) {
                        if ((result = this.mainValue.compareTo(that.getMainValue())) == 0) {
                            result = this.sku.compareTo(that.getSku());
                        }
                    }
                }
            }
        }
        return result;
    }

}