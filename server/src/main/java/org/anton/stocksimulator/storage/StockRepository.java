package org.anton.stocksimulator.storage;

import org.anton.stocksimulator.entity.Stock;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StockRepository extends CrudRepository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);
}
