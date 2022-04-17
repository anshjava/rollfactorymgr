package ru.kamuzta.rollfactorymgr.model.roll;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.utils.RollCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonPropertyOrder({"id", "sku", "rollType", "paper", "widthType", "coreType", "mainValue", "state"})
public class Roll implements Comparable<Roll> {
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
    @NotNull
    private RollState state;

    //copy constructor
    public Roll(Roll that) {
        this.id = that.id;
        this.sku = that.sku;
        this.rollType = that.rollType;
        this.paper = that.paper;
        this.widthType = that.widthType;
        this.coreType = that.coreType;
        this.mainValue = that.mainValue;
        this.state = that.state;
    }

    @Override
    public String toString() {
        return String.format("Roll: [%s] [%d] [%s] %s %s %.0f x %.1f%s x %.0f %.0fg/m2",
                state,
                id,
                sku,
                rollType,
                paper,
                widthType.getWidth(),
                rollType == RollType.LENGTH
                ? RollCalculator.calculateLength(this)
                : RollCalculator.calculateDiameter(this),
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
    // state (ACTIVE first)
    @Override
    public int compareTo(@NotNull Roll that) {
        return ComparisonChain.start()
                .compare(rollType, that.rollType)
                .compare(paper, that.paper)
                .compare(widthType, that.widthType)
                .compare(coreType, that.coreType)
                .compare(mainValue, that.mainValue)
                .compare(sku, that.sku)
                .compare(state, that.state)
                .result();
    }

}