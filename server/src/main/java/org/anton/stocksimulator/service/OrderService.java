package org.anton.stocksimulator.service;

import org.anton.stocksimulator.dto.OrderRequest;
import org.anton.stocksimulator.entity.Order;

public interface OrderService {
    Order sell(OrderRequest request);
    Order buy(OrderRequest request);
    void cancel(Long orderId);
}
