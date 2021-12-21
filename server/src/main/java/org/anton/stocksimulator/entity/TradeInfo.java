package org.anton.stocksimulator.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeInfo extends Trade {
    private String symbol;

    public TradeInfo(Trade trade, String symbol) {
        super(trade.getId(), trade.getSellOrderId(), trade.getBuyOrderId(), trade.getQuantity(), trade.getPrice(), trade.getDateCreated());
        this.symbol = symbol;
    }

}
