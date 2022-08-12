package ru.kamuzta.rollfactorymgr.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.ComparisonChain;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.utils.RollCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
@JsonPropertyOrder({"id", "creationDateTime", "client", "state", "lines"})
public class Order implements Comparable<Order> {
    @NotNull
    private Long id;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDateTime;

    @NotNull
    private Client client;

    @NotNull
    private OrderState state;

    @NotNull
    private List<OrderLine> lines;

    //copy constructor
    public Order(Order that) {
        this.id = that.id;
        this.creationDateTime = that.creationDateTime;
        this.client = that.client;
        this.state = that.state;
        this.lines = that.lines.stream().map(OrderLine::new).collect(Collectors.toList());
    }

    public BigDecimal calculateWeight() {
        return lines.stream().map(line -> RollCalculator.calculateWeight(line.getRoll()).multiply(BigDecimal.valueOf(line.getQuantity()))
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
                .compare(creationDateTime, that.creationDateTime)
                .compare(client.getCompanyName(), that.client.getCompanyName())
                .compare(id, that.id)
                .result();
    }

    @Override
    public String toString() {
        return String.format("Order: [%d %s %s] [%s] [%dlines %dpcs %.0fkg]",
                id,
                creationDateTime,
                state,
                client.getCompanyName(),
                calculateLinesCount(),
                calculateQuantity(),
                calculateWeight());
    }
}
