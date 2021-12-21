package org.anton.stocksimulator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.anton.stocksimulator.entity.*;
import org.anton.stocksimulator.storage.OrderRepository;
import org.anton.stocksimulator.storage.StockRepository;
import org.anton.stocksimulator.storage.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @InjectMocks
    private MatchingServiceImpl matchingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private TransactionTemplate transactionTemplate;

    private String topic = "topic";

    private Stock stock1 = new Stock(1L, "AAPL");
    private Stock stock2 = new Stock(2L, "GOOG");

    private long tradeId = 1L;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(matchingService, "topic", topic);
        ReflectionTestUtils.setField(matchingService, "transactionTemplate", transactionTemplate);
    }

    @Test
    public void test() {

        var orders = orders();

        when(orderRepository.findAllByActiveTrue()).thenReturn(orders);
        when(stockRepository.findById(stock1.getId())).thenReturn(Optional.of(stock1));
        when(stockRepository.findById(stock2.getId())).thenReturn(Optional.of(stock2));
        when(transactionTemplate.execute(any()))
                .thenAnswer(invocation -> invocation.<TransactionCallback<Boolean>>getArgument(0).doInTransaction(any(TransactionStatus.class)));
        when(tradeRepository.save(any(Trade.class)))
                .thenAnswer(invocation -> {
                    var currentTrade = (Trade)invocation.getArgument(0);
                    ReflectionTestUtils.setField(currentTrade, "id", tradeId++);
                    return currentTrade;
                });

        matchingService.process();

        ArgumentCaptor<String> tradeArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(messagingTemplate, times(4)).convertAndSend(eq(topic), tradeArgumentCaptor.capture());

        ObjectMapper objectMapper = new ObjectMapper();

        var trades = tradeArgumentCaptor.getAllValues().stream().map(s->{
            try {
                return objectMapper.readValue(s, TradeInfo.class);
            } catch (JsonProcessingException e) {
                fail(e);
            }
            return null;
        }).toList();

        assertEquals(4, trades.size());

        assertAll(
                ()-> assertEquals(1L, trades.get(0).getId()),
                ()-> assertEquals(2L, trades.get(0).getBuyOrderId()),
                ()-> assertEquals(3L, trades.get(0).getSellOrderId()),
                ()-> assertEquals(10, trades.get(0).getQuantity()),
                ()-> assertEquals(80, trades.get(0).getPrice()),
                ()-> assertEquals(2L, trades.get(1).getId()),
                ()-> assertEquals(4L, trades.get(1).getBuyOrderId()),
                ()-> assertEquals(3L, trades.get(1).getSellOrderId()),
                ()-> assertEquals(10, trades.get(1).getQuantity()),
                ()-> assertEquals(60, trades.get(1).getPrice()),
                ()-> assertEquals(3L, trades.get(2).getId()),
                ()-> assertEquals(4L, trades.get(2).getBuyOrderId()),
                ()-> assertEquals(1L, trades.get(2).getSellOrderId()),
                ()-> assertEquals(20, trades.get(2).getQuantity()),
                ()-> assertEquals(60, trades.get(2).getPrice()),
                ()-> assertEquals(4L, trades.get(3).getId()),
                ()-> assertEquals(6L, trades.get(3).getBuyOrderId()),
                ()-> assertEquals(7L, trades.get(3).getSellOrderId()),
                ()-> assertEquals(10, trades.get(3).getQuantity()),
                ()-> assertEquals(20, trades.get(3).getPrice())
        );

        verify(orderRepository, times(4)).saveAll(any());
    }

    private List<Order> orders() {
        var order1 = new Order(100, 50, OrderType.SELL, stock1.getId());
        order1.setId(1L);
        var order2 = new Order(10, 80, OrderType.BUY, stock1.getId());
        order2.setId(2L);
        var order3 = new Order(20, 60, OrderType.SELL, stock1.getId());
        order3.setId(3L);
        var order4 = new Order(30, 60, OrderType.BUY, stock1.getId());
        order4.setId(4L);
        var order5 = new Order(30, 10, OrderType.BUY, stock1.getId());
        order5.setId(5L);
        var order6 = new Order(10, 20, OrderType.BUY, stock2.getId());
        order6.setId(6L);
        var order7 = new Order(20, 20, OrderType.SELL, stock2.getId());
        order7.setId(7L);

        return List.of(order1, order2, order3, order4, order5, order6, order7);
    }

}