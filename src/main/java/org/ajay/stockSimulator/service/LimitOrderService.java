package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.Repo.LimitOrderRepo;
import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@EnableScheduling
public class LimitOrderService {

    private static final Lock PROCESS_LOCK = new ReentrantLock();

    @Autowired
    private LimitOrderRepo orderRepo;
    @Autowired
    private StockRepo stockRepo;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public LimitOrder placeLimitOrder(String username,
                                      String stockSymbol,
                                      Integer quantity,
                                      BigDecimal targetPrice,
                                      TransactionType type) {

        LimitOrder order = new LimitOrder();
        order.setUsername(username);
        order.setStockSymbol(stockSymbol);
        order.setPrice(targetPrice);
        order.setType(type);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setQuantity(quantity);
        order.setRemainingQuantity(quantity);
        return orderRepo.save(order);
    }

    @Transactional
    protected void executeOrder(LimitOrder order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }

        try {
            if (order.getType() == TransactionType.BUY) {
                transactionService.buyStock(
                        order.getUsername(),
                        order.getStockSymbol(),
                        order.getQuantity()
                );
            } else {
                User user = userRepo.findByUsername(order.getUsername());
                if (user == null) {
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepo.save(order);
                    return;
                }
                transactionService.sellStock(
                        user.getUserId(),
                        order.getStockSymbol(),
                        order.getQuantity()
                );
            }
            order.setStatus(OrderStatus.EXECUTED);
        } catch (Exception e) {
            order.setStatus(OrderStatus.CANCELLED);
        }
        orderRepo.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, String username) {
        LimitOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized cancellation");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
    }

    public List<LimitOrder> getOrderBook(String symbol) {
        return orderRepo.findByStockSymbolAndStatus(
                symbol,
                OrderStatus.PENDING
        );
    }

    // FIXED: Added lock to prevent multiple instances from running simultaneously
    @Scheduled(fixedRate = 100000) //  100 seconds)
    @Transactional
    public void processOrderBook() {
        // Only one instance can execute this at a time
        if (!PROCESS_LOCK.tryLock()) {
            System.out.println("Another instance is processing order book, skipping...");
            return;
        }

        try {
            List<String> symbols = orderRepo.findDistinctSymbols();
            System.out.println("Processing order book for " + symbols.size() + " symbols");

            for (String symbol : symbols) {
                matchOrders(symbol);
            }
        } finally {
            PROCESS_LOCK.unlock();
        }
    }

    @Transactional
    public void matchOrders(String symbol) {
        List<LimitOrder> buyOrders =
                orderRepo.findByStockSymbolAndTypeAndStatusOrderByPriceDescCreatedAtAsc(
                        symbol, TransactionType.BUY, OrderStatus.PENDING
                );

        List<LimitOrder> sellOrders =
                orderRepo.findByStockSymbolAndTypeAndStatusOrderByPriceAscCreatedAtAsc(
                        symbol, TransactionType.SELL, OrderStatus.PENDING
                );

        int i = 0, j = 0;

        while (i < buyOrders.size() && j < sellOrders.size()) {
            LimitOrder buy = buyOrders.get(i);
            LimitOrder sell = sellOrders.get(j);

            if (buy.getPrice().compareTo(sell.getPrice()) < 0) {
                break;
            }

            int tradeQty = Math.min(
                    buy.getRemainingQuantity(),
                    sell.getRemainingQuantity()
            );

            BigDecimal executionPrice = sell.getPrice();

            transactionService.settleMatchedTrade(
                    buy.getUsername(),
                    sell.getUsername(),
                    symbol,
                    tradeQty,
                    executionPrice
            );

            buy.setRemainingQuantity(
                    buy.getRemainingQuantity() - tradeQty
            );

            sell.setRemainingQuantity(
                    sell.getRemainingQuantity() - tradeQty
            );

            if (buy.getRemainingQuantity() == 0) {
                buy.setStatus(OrderStatus.EXECUTED);
                i++;
            } else {
                buy.setStatus(OrderStatus.PARTIAL);
            }

            if (sell.getRemainingQuantity() == 0) {
                sell.setStatus(OrderStatus.EXECUTED);
                j++;
            } else {
                sell.setStatus(OrderStatus.PARTIAL);
            }

            orderRepo.save(buy);
            orderRepo.save(sell);
        }

        // Broadcast order book update
        List<LimitOrder> remainingOrders =
                orderRepo.findByStockSymbolAndStatus(
                        symbol,
                        OrderStatus.PENDING
                );

        List<Map<String, Object>> bids = remainingOrders.stream()
                .filter(o -> o.getType() == TransactionType.BUY)
                .sorted((a, b) -> b.getPrice().compareTo(a.getPrice()))
                .map(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("price", o.getPrice());
                    map.put("quantity", o.getRemainingQuantity());
                    return map;
                })
                .toList();

        List<Map<String, Object>> asks = remainingOrders.stream()
                .filter(o -> o.getType() == TransactionType.SELL)
                .sorted((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .map(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("price", o.getPrice());
                    map.put("quantity", o.getRemainingQuantity());
                    return map;
                })
                .toList();

        Map<String, Object> payload = new HashMap<>();
        payload.put("bids", bids);
        payload.put("asks", asks);

        messagingTemplate.convertAndSend(
                "/topic/orderbook/" + symbol,
                payload
        );
    }
}