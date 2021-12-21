package org.anton.stocksimulator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Version;

import java.time.Instant;

@Data
public class Order {
    @Id
    private Long id;
    @Version
    @Setter(AccessLevel.PROTECTED)
    @JsonIgnore
    private int version;
    private Integer quantity;
    private Integer price;
    @JsonIgnore
    private Integer remain;
    private OrderType type;
    @JsonIgnore
    private Long stockId;
    @JsonIgnore
    private boolean active = true;
    @Setter(AccessLevel.PROTECTED)
    @ReadOnlyProperty
    private Instant dateCreated;

    public Order(int quantity, int price, OrderType type, Long stockId) {
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.stockId = stockId;
        this.remain = quantity;
    }

}
