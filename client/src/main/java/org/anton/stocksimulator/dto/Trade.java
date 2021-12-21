package org.anton.stocksimulator.dto;

import java.time.Instant;

public class Trade {

    private Long id;
    private Long sellOrderId;
    private Long buyOrderId;
    private int quantity;
    private int price;
    private Instant dateCreated;
    private String symbol;

    public Trade() {
    }

    public Long getId() {
        return id;
    }

    public Long getSellOrderId() {
        return sellOrderId;
    }

    public Long getBuyOrderId() {
        return buyOrderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public String getSymbol() {
        return symbol;
    }
}