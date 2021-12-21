package org.anton.stocksimulator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    @Id Long id;
    String symbol;

    public Stock(String symbol) {
        this(null, symbol);
    }

}
