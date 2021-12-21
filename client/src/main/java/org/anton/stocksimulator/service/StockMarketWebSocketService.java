package org.anton.stocksimulator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.anton.stocksimulator.dto.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;

@Service
public class StockMarketWebSocketService {

    private static Logger logger = LoggerFactory.getLogger(StockMarketWebSocketService.class);

    @Value("${websocket.topic.name}")
    private String topic;
    @Value("${websocket.host}")
    private String socketUrl;
    private WebSocketStompClient stompClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String msgTemplate = "New execution with id %s: %s %s @ %s (order %s and %s)";



    @PostConstruct
    public void start() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        stompClient.connect(socketUrl, new SocketHandler());
    }

    class SocketHandler extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(topic, this);
        }
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            try {
                var trade = objectMapper.readValue((String) payload, Trade.class);
                System.out.printf((msgTemplate) + "%n", trade.getId(), trade.getSymbol(), trade.getQuantity(), trade.getPrice(), trade.getSellOrderId(), trade.getBuyOrderId());
            } catch (JsonProcessingException e) {
                logger.error("Can't parse Trade JSON");
            }
        }
    }

}
