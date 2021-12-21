package org.anton.stocksimulator;

import org.anton.stocksimulator.service.StockMarketApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.Scanner;

@SpringBootApplication
public class ConsoleApplication implements CommandLineRunner {

    static final String orderMsgTemplate = "[%s] Order with id %s added: %s %s %s @ %s";

    @Autowired
    private StockMarketApiService apiService;

    private Long lastId;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConsoleApplication.class);
        // disable spring banner
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello!");
        System.out.println("Make an order: add GOOG B 100 50");
        System.out.println("Cancel last order: cancel");
        System.out.println("Exit: exit");

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            processLine(line);
        }
    }

    private void processLine(String line) {
        try {
            if ("exit".equals(line)) {
                System.out.println("Buy!");
                System.exit(1);
            }
            if ("cancel".equals(line)) {
                if (lastId != null) {
                    apiService.cancel(lastId);
                    System.out.println("[%s] Order with id %s canceled.".formatted(Instant.now(), lastId));
                } else {
                    System.out.println("You must place an order before canceling it!");
                }
                return;
            }
            if (line.startsWith("add")) {
                String[] tokens = line.split(" ");
                String symbol = tokens[1];
                String operation = tokens[2];
                int quantity = Integer.parseInt(tokens[3]);
                int price = Integer.parseInt(tokens[4]);

                if ("B".equalsIgnoreCase(operation)) {
                    final var order = apiService.buy(symbol, quantity, price);
                    lastId = order.getId();
                    System.out.println(orderMsgTemplate.formatted(order.getDateCreated(), order.getId(), symbol, "Buy", order.getQuantity(), order.getPrice()));
                } else if ("S".equalsIgnoreCase(operation)) {
                    final var order = apiService.sell(symbol, quantity, price);
                    lastId = order.getId();
                    System.out.println(orderMsgTemplate.formatted(order.getDateCreated(), order.getId(), symbol, "Sell", order.getQuantity(), order.getPrice()));

                } else {
                    System.out.println("Operation is not recognised!");
                }

            } else {
                System.out.println("I don't understand you)");
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: "+e.getCause());
        }
    }
}