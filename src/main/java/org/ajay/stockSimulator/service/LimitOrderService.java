package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.Repo.LimitOrderRepo;
import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
public class LimitOrderService {
    @Autowired
    private  LimitOrderRepo orderRepo;
    @Autowired
    private  StockRepo stockRepo;
    @Autowired
    private  TransactionService transactionService;
    @Autowired
    private  UserRepo userRepo;

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

//    @Scheduled(fixedRate = 5000)
//    @Transactional
//    public void processPendingOrders() {
//
//        List<LimitOrder> pendingOrders =
//                orderRepo.findByStatus(OrderStatus.PENDING);
//
//        for (LimitOrder order : pendingOrders) {
//
//            //  Idempotency guard
//            if (order.getStatus() != OrderStatus.PENDING) {
//                continue;
//            }
//
//            Stock stock = stockRepo.findById(order.getStockSymbol())
//                    .orElse(null);
//
//            if (stock == null) {
//                order.setStatus(OrderStatus.CANCELLED);
//                orderRepo.save(order);
//                continue;
//            }
//
//            BigDecimal currentPrice = stock.getCurrentprice();
//
//            boolean shouldExecute = false;
//
//            if (order.getType() == TransactionType.BUY &&
//                    currentPrice.compareTo(order.getPrice()) <= 0) {
//                shouldExecute = true;
//            }
//
//            if (order.getType() == TransactionType.SELL &&
//                    currentPrice.compareTo(order.getPrice()) >= 0) {
//                shouldExecute = true;
//            }
//
//            if (shouldExecute) {
//                executeOrder(order);
//            }
//        }
//    }
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

            // Prevent stuck PENDING orders
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

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void processOrderBook() {

        List<String> symbols = orderRepo.findDistinctSymbols();

        for (String symbol : symbols) {
            matchOrders(symbol);
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

            //  Price match condition
            if (buy.getPrice().compareTo(sell.getPrice()) < 0) {
                break;
            }

            int tradeQty = Math.min(
                    buy.getRemainingQuantity(),
                    sell.getRemainingQuantity()
            );

            //  Match at sell price (standard exchange rule)
            BigDecimal executionPrice = sell.getPrice();

            transactionService.settleMatchedTrade(
                    buy.getUsername(),
                    sell.getUsername(),
                    symbol,
                    tradeQty,
                    executionPrice
            );

            // Update remaining quantities
            buy.setRemainingQuantity(
                    buy.getRemainingQuantity() - tradeQty
            );

            sell.setRemainingQuantity(
                    sell.getRemainingQuantity() - tradeQty
            );

            // Update order statuses
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
    }


}