package org.anton.stocksimulator.storage;

import org.anton.stocksimulator.entity.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
public class StockRepositoryTest {

    @Autowired
    private StockRepository repository;

    @Test
    public void test() {
        Stock stock = new Stock(null, "AAPL");
        Stock saved = repository.save(stock);
        Optional<Stock> result = repository.findById(saved.getId());
        assertAll(
                ()->assertTrue(result.isPresent()),
                ()->assertEquals(stock.getSymbol(), result.get().getSymbol())
        );
    }
}
