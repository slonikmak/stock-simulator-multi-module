package org.anton.stocksimulator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.anton.stocksimulator.entity.*;
import org.anton.stocksimulator.storage.OrderRepository;
import org.anton.stocksimulator.storage.StockRepository;
import org.anton.stocksimulator.storage.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService{

    private final Logger logger = LoggerFactory.getLogger(MatchingServiceImpl.class);

    @Value("${websocket.topic.name}")
    private String topic;

    private final MessageSendingOperations<String>  messagingTemplate;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
    private final PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    private final Comparator<Order> orderComparator = Comparator.comparing(Order::getPrice).reversed().thenComparing(Order::getDateCreated);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    @Scheduled(fixedDelay = 1000)
    public void process() {
        logger.info("Process orders");

        final var collectedByStock = orderRepository.findAllByActiveTrue()
                .stream()
                .collect(Collectors.groupingBy(Order::getStockId));

        collectedByStock.forEach((stockId, orders) -> {
            stockRepository.findById(stockId)
                    .map(Stock::getSymbol)
                    .ifPresentOrElse(symbol -> {
                        final var collectedByType = orders.stream().collect(Collectors.groupingBy(Order::getType));
                        if (Objects.nonNull(collectedByType.get(OrderType.SELL)) && Objects.nonNull(collectedByType.get(OrderType.BUY))) {
                            final var sells = collectedByType.get(OrderType.SELL).stream().sorted(orderComparator).toList();
                            final var buys = collectedByType.get(OrderType.BUY).stream().sorted(orderComparator).toList();

                            logger.info("Process '{}' orders. Sell count: {}, buy count: {}", symbol, sells.size(), buys.size());

                            sells.forEach(sell -> {
                                final var completed = buys.stream()
                                        .filter(Order::isActive)
                                        .filter(buy -> buy.getPrice() >= sell.getPrice())
                                        .takeWhile(buy -> {
                                            int quantity = buy.getRemain() > sell.getRemain() ? sell.getRemain() : buy.getRemain();
                                            if (quantity > 0) {
                                                buy.setRemain(buy.getRemain() - quantity);
                                                sell.setRemain(sell.getRemain() - quantity);
                                                final var trade = saveTrade(buy, sell, quantity);

                                                logger.info("Trade: {}, {} @ {}", symbol, trade.getQuantity(), trade.getPrice());

                                                sendMessage(trade, symbol);
                                                return true;
                                            } else {
                                                return false;
                                            }
                                        }).toList();
                            });
                        }

                    }, () -> logger.error("Stock with id '{}' not found", stockId));


        });
    }

    private Trade saveTrade(Order buy, Order sell, int quantity) {
        prepareOrder(sell);
        prepareOrder(buy);
        final var trade = new Trade(sell.getId(), buy.getId(), quantity, buy.getPrice());
        return transactionTemplate.execute(status -> {
            orderRepository.saveAll(List.of(buy, sell));
            return tradeRepository.save(trade);
        });
    }

    private void sendMessage(Trade trade, String symbol) {
        try {
            var tradeInfo = new TradeInfo(trade, symbol);
            messagingTemplate.convertAndSend(topic, objectMapper.writeValueAsString(tradeInfo));
        } catch (JsonProcessingException e) {
            logger.error("Cant convert object to JSON");
        }
    }

    private void prepareOrder(Order order) {
        if (order.getRemain() == 0) {
            order.setActive(false);
        }
    }

}
