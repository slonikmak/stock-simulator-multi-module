package org.anton.stocksimulator.storage;

import org.anton.stocksimulator.entity.Order;
import org.anton.stocksimulator.entity.OrderType;
import org.anton.stocksimulator.entity.Stock;
import org.anton.stocksimulator.entity.Trade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
public class TradeRepositoryTest {

    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void test() {
        var stock = stockRepository.save(new Stock("AAPL"));
        var sellOrder = orderRepository.save(new Order(10, 20, OrderType.SELL, stock.getId()));
        var buyOrder = orderRepository.save(new Order(10, 20, OrderType.BUY, stock.getId()));
        var trade = tradeRepository.save(new Trade(sellOrder.getId(), buyOrder.getId(), 10, 20));

        assertNotNull(trade);

        assertAll(
                ()->assertNotNull(trade.getId()),
                ()->assertEquals(sellOrder.getId(), trade.getSellOrderId()),
                ()->assertEquals(buyOrder.getId(), trade.getBuyOrderId())
        );
    }


}
