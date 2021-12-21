package org.anton.stocksimulator.dto;

public class OrderRequest {
    private int quantity;
    private int price;
    private String stock;

    public OrderRequest() {
    }

    public OrderRequest(int quantity, int price, String stock) {
        this.quantity = quantity;
        this.price = price;
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getStock() {
        return stock;
    }
}
