package org.anton.stocksimulator.service;

import lombok.RequiredArgsConstructor;
import org.anton.stocksimulator.dto.OrderRequest;
import org.anton.stocksimulator.entity.Order;
import org.anton.stocksimulator.entity.OrderType;
import org.anton.stocksimulator.entity.Stock;
import org.anton.stocksimulator.storage.OrderRepository;
import org.anton.stocksimulator.storage.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private static Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    @Override
    public Order sell(OrderRequest request) {
        logger.info("Received sell order: {}", request);
        var stock = getStockBySymbol(request.getStock());
        var order = new Order(request.getQuantity(), request.getPrice(), OrderType.SELL, stock.getId());
        final var saved = orderRepository.save(order);
        return orderRepository.findById(saved.getId()).orElseThrow();
    }

    @Override
    public Order buy(OrderRequest request) {
        logger.info("Received buy order: {}", request);
        var stock = getStockBySymbol(request.getStock());
        var order = new Order(request.getQuantity(), request.getPrice(), OrderType.BUY, stock.getId());
        final var saved = orderRepository.save(order);
        return orderRepository.findById(saved.getId()).orElseThrow();
    }

    @Override
    public void cancel(Long orderId) {
        logger.info("Cancel order '{}'", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setActive(false);
        orderRepository.save(order);
    }

    private Stock getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol).orElseGet(()->{
            var stock = new Stock(null, symbol);
            logger.info("Add new stock symbol '{};", symbol);
            return stockRepository.save(stock);
        });
    }

}
