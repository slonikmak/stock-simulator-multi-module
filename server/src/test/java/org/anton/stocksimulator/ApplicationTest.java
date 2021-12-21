package org.anton.stocksimulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.anton.stocksimulator.dto.OrderRequest;
import org.anton.stocksimulator.entity.Order;
import org.anton.stocksimulator.entity.Trade;
import org.anton.stocksimulator.entity.TradeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @LocalServerPort
    private int port;

    @Value("${websocket.topic.name}")
    private String topic;

    @Value("${websocket.endpoint.name}")
    private String endpoint;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private String socketUrl;
    private String apiUrl;
    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        socketUrl = "ws://localhost:%s/%s".formatted(port, endpoint);
        apiUrl = "http://localhost:%s/".formatted(port);
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
    }

    @Test
    public void test() {

        TestHandler sessionHandler = new TestHandler(1);
        stompClient.connect(socketUrl, sessionHandler);

        final var order1 = restTemplate.postForObject(apiUrl + "buy", new OrderRequest(10, 20, "AAPL"), Order.class);
        final var order2 = restTemplate.postForObject(apiUrl + "sell", new OrderRequest(20, 20, "AAPL"), Order.class);

        try {
            final var trade = sessionHandler.getFuture().get(3, TimeUnit.SECONDS);
            assertAll(
                    ()-> assertEquals(10, trade.getQuantity()),
                    ()-> assertEquals(20, trade.getPrice()),
                    ()-> assertEquals(order2.getId(), trade.getSellOrderId()),
                    ()-> assertEquals(order1.getId(), trade.getBuyOrderId())
            );
        } catch (Exception e) {
            fail(e);
        }
    }

    class TestHandler extends StompSessionHandlerAdapter {

        private final CompletableFuture<TradeInfo> completableFuture = new CompletableFuture<>();
        private int messageCount;

        public TestHandler(int messageCount) {
            this.messageCount = messageCount;
        }

        public CompletableFuture<TradeInfo> getFuture() {
            return completableFuture;
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(topic, this);
        }
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            messageCount--;
            if (messageCount == 0) {
                try {
                    var trade = objectMapper.readValue((String) payload, TradeInfo.class);
                    completableFuture.complete(trade);
                } catch (JsonProcessingException e) {
                    fail(e);
                }
            }
        }
    }

}
