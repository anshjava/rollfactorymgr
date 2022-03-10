package ru.kamuzta.rollfactorymgr.model;

import com.google.common.collect.ComparisonChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.model.client.Client;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Order
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
public class Order implements Comparable<Order> {
    @NotNull
    private Long id;

    @NotNull
    private ZonedDateTime creationDate;

    @NotNull
    private OrderState orderState;

    @NotNull
    private Client client;

    @NotNull
    private List<OrderLine> orderLines;

    //sort Order by:
    // time (older first)
    // client (by name natural)
    // order state (NEW first)
    // id (natural)
    @Override
    public int compareTo(@NotNull Order that) {
        return ComparisonChain.start()
                .compare(creationDate, that.creationDate)
                .compare(client.getCompanyName(), that.client.getCompanyName())
                .compare(orderState, that.orderState)
                .compare(id, that.id)
                .result();
    }
}
