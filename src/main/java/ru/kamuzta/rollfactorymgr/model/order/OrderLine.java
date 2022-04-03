package ru.kamuzta.rollfactorymgr.model.order;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.model.roll.Roll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;

/**
 * Order Line
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonPropertyOrder({"id", "roll", "quantity", "state"})
public class OrderLine implements Comparable<OrderLine> {
    @NotNull
    private Long id;
    @NotNull
    private Roll roll;
    @NotNull
    private Integer quantity;
    @NotNull
    private OrderState state;

    public OrderLine(OrderLine that) {
        this.id = that.id;
        this.roll = that.roll;
        this.quantity = that.quantity;
        this.state = that.state;
    }

    public BigDecimal calculateWeight() {
        return roll.calculateWeight().multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    //sort Order Line by:
    // state (NEW first)
    // roll (by paper - NTC44 first)
    // roll (by width - 57 first)
    // quantity (reversed)
    // roll (by weight reversed)
    // id (natural)
    @Override
    public int compareTo(@NotNull OrderLine that) {
        return ComparisonChain.start()
                .compare(state, that.state)
                .compare(roll.getPaper(), that.roll.getPaper())
                .compare(roll.getWidthType(), that.roll.getWidthType())
                .compare(quantity, that.quantity, Comparator.reverseOrder())
                .compare(roll.calculateWeight(), that.roll.calculateWeight(), Comparator.reverseOrder())
                .compare(id, that.id)
                .result();
    }

    @Override
    public String toString() {
        return String.format("orderLine: [%d] [%s] [%s %s %.0f] [%dpcs %.0fkg]",
                id,
                state,
                roll.getSku(),
                roll.getPaper().getCode(),
                roll.getWidthType().getWidth(),
                quantity,
                calculateWeight());
    }
}
