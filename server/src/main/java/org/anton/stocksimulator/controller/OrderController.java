package org.anton.stocksimulator.controller;

import lombok.RequiredArgsConstructor;
import org.anton.stocksimulator.dto.OrderRequest;
import org.anton.stocksimulator.entity.Order;
import org.anton.stocksimulator.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping(path = "/buy")
    public Order buyStocks(@RequestBody OrderRequest orderRequest) {
        return service.buy(orderRequest);
    }

    @PostMapping(path = "/sell")
    public Order sellStocks(@RequestBody OrderRequest orderRequest) {
        return service.sell(orderRequest);
    }

    @PutMapping(path = "/cancel/{id}")
    public void cancel(@PathVariable Long id) {
        service.cancel(id);
    }
}
