package org.anton.stocksimulator.storage;

import org.anton.stocksimulator.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findAllByActiveTrue();
}
