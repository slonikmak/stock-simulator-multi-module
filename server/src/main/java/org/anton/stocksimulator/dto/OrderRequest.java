package org.anton.stocksimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class OrderRequest {
    private int quantity;
    private int price;
    private String stock;
}
