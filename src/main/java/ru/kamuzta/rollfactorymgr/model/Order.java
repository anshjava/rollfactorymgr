package ru.kamuzta.rollfactorymgr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Order
 */
@AllArgsConstructor
@Getter
@Builder
public class Order implements Comparable<Order> {
    @NotNull
    private Long id;

    @NotNull
    private ZonedDateTime zonedDateTime;

    @NotNull
    OrderState orderState;

    @NotNull
    Client client;

    @NotNull
    List<OrderLine> orderLines;

    //sort Order by:
    // time (older first)
    // client (by name natural)
    // order state (NEW first)
    // id (natural)
    @Override
    public int compareTo(@NotNull Order that) {
        int result = 0;

        if (this.equals(that)) {
            return result;
        } else if ((result = this.zonedDateTime.compareTo(that.getZonedDateTime())) == 0) {
            if ((result = this.client.getName().compareTo(that.getClient().getName())) == 0) {
                if ((result = this.orderState.compareTo(that.getOrderState())) == 0) {
                    result = this.id.compareTo(that.getId());
                }
            }
        }
        return result;
    }
}
