package ru.kamuzta.rollfactorymgr.model.order;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.model.client.Client;

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

    @Override
    public Order clone() {
        return new Order(this.id, this.creationDate, this.client, this.lines.stream().map(OrderLine::clone).collect(Collectors.toList()), this.state);
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
}
