package org.anton.stocksimulator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    @Id
    private Long id;
    private Long sellOrderId;
    private Long buyOrderId;
    private int quantity;
    private int price;
    @ReadOnlyProperty
    private Instant dateCreated;

    public Trade(Long sellOrder, Long buyOrder, int quantity, int price) {
        this(null, sellOrder, buyOrder, quantity, price, null);
    }
}
