package org.anton.stocksimulator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.anton.stocksimulator.dto.Order;
import org.anton.stocksimulator.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class StockMarketApiServiceImpl implements StockMarketApiService{

    @Value("${api.host}")
    private String apiUrl;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public StockMarketApiServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Order buy(String symbol, int quantity, int price) {
        return makeOrder("/buy", symbol, quantity, price);
    }

    @Override
    public Order sell(String symbol, int quantity, int price) {
        return makeOrder("/sell", symbol, quantity, price);
    }

    private Order makeOrder(String endpoint, String symbol, int quantity, int price) {
        final var request = new OrderRequest(quantity, price, symbol);
        try {
            String json = objectMapper.writeValueAsString(request);
            HttpEntity<String> httpEntity = new HttpEntity<>(json, headers());
            final var orderResponseEntity = restTemplate.postForEntity(apiUrl+endpoint, httpEntity, String.class);
            if (orderResponseEntity.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error while send request");
            }
            return objectMapper.readValue(orderResponseEntity.getBody(), Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while parsing json");
        }
    }

    @Override
    public void cancel(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        restTemplate.put(apiUrl+"/cancel/{id}", entity, id);
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

}
