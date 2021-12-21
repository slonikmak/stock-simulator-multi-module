package org.anton.stocksimulator.dto;

import java.time.Instant;

public class Order {
    private Long id;
    private Integer quantity;
    private Integer price;
    private String type;
    private Instant dateCreated;

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }
}
