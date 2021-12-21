package org.anton.stocksimulator.service;

import org.anton.stocksimulator.dto.Order;

public interface StockMarketApiService {
    Order buy(String symbol, int quantity, int price);
    Order sell(String symbol, int quantity, int price);
    void cancel(Long id);
}
