package ru.kamuzta.rollfactorymgr.model.order;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.model.client.Client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonPropertyOrder({"id", "creationDate", "client", "lines", "state"})
public class Order implements Comparable<Order> {
    @NotNull
    private Long id;

    @NotNull
    private OffsetDateTime creationDate;

    @NotNull
    private Client client;

    @NotNull
    private List<OrderLine> lines;

    @NotNull
    private OrderState state;

    public Order(Order that) {
        this.id = that.id;
        this.creationDate = that.creationDate;
        this.client = that.client;
        this.lines = that.lines.stream().map(OrderLine::new).collect(Collectors.toList());
        this.state = that.state;
    }

    public BigDecimal calculateWeight() {
        return lines.stream().map(line -> line.getRoll().calculateWeight().multiply(BigDecimal.valueOf(line.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer calculateQuantity() {
        return lines.stream().map(OrderLine::getQuantity)
                .reduce(0, Integer::sum);
    }

    public Integer calculateLinesCount() {
        return lines.size();
    }

    //sort Order by:
    // order state (NEW first)
    // time (older first)
    // client (by name natural)
    // id (natural)
    @Override
    public int compareTo(@NotNull Order that) {
        return ComparisonChain.start()
                .compare(state, that.state)
                .compare(creationDate, that.creationDate)
                .compare(client.getCompanyName(), that.client.getCompanyName())
                .compare(id, that.id)
                .result();
    }

    @Override
    public String toString() {
        return String.format("Order: [%d %s %s] [%s] [%dlines %dpcs %.0fkg]",
                id,
                creationDate,
                state,
                client.getCompanyName(),
                calculateLinesCount(),
                calculateQuantity(),
                calculateWeight());
    }
}
