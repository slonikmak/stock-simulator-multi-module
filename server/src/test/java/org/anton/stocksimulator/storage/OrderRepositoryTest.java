package org.anton.stocksimulator.storage;

import lombok.RequiredArgsConstructor;
import org.anton.stocksimulator.entity.Order;
import org.anton.stocksimulator.entity.OrderType;
import org.anton.stocksimulator.entity.Stock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;
    @Autowired
    private StockRepository stockRepository;

    @Test
    public void test() {

        var stock = new Stock("AAPL");
        stock = stockRepository.save(stock);
        var order = new Order(4, 5, OrderType.BUY, stock.getId());
        var saved = repository.save(order);
        var result = repository.findById(saved.getId());

        var notActiveOrder = new Order(5, 4, OrderType.SELL, stock.getId());
        notActiveOrder.setActive(false);
        repository.save(notActiveOrder);

        var active = repository.findAllByActiveTrue();

        assertAll(
                ()-> assertTrue(result.isPresent()),
                ()-> assertEquals(order.getPrice(), result.get().getPrice()),
                ()-> assertEquals(order.getStockId(), result.get().getStockId()),
                ()-> assertEquals(order.getType(), result.get().getType()),
                ()-> assertNotNull(result.get().getDateCreated()),
                ()-> assertEquals(1, active.size()),
                ()-> assertEquals(saved.getId(), active.get(0).getId())
        );

    }

}