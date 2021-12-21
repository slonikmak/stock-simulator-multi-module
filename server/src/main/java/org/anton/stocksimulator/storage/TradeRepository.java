package org.anton.stocksimulator.storage;

import org.anton.stocksimulator.entity.Trade;
import org.springframework.data.repository.CrudRepository;

public interface TradeRepository extends CrudRepository<Trade, Long> {
}
