package org.ajay.stockSimulator.Repo;

import org.ajay.stockSimulator.model.LimitOrder;
import org.ajay.stockSimulator.model.OrderStatus;
import org.ajay.stockSimulator.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LimitOrderRepo
        extends JpaRepository<LimitOrder, Long> {

    List<LimitOrder> findByStatus(OrderStatus status);
    List<LimitOrder> findByStockSymbolAndStatus(
            String stockSymbol,
            OrderStatus status
    );

    // BUY side: highest price first
    List<LimitOrder> findByStockSymbolAndTypeAndStatusOrderByPriceDescCreatedAtAsc(
            String symbol,
            TransactionType type,
            OrderStatus status
    );

    // SELL side: lowest price first
    List<LimitOrder> findByStockSymbolAndTypeAndStatusOrderByPriceAscCreatedAtAsc(
            String symbol,
            TransactionType type,
            OrderStatus status
    );

    @Query("SELECT DISTINCT l.stockSymbol FROM LimitOrder l")
    List<String> findDistinctSymbols();

    long countByUsernameAndStatusInAndCreatedAtBetween(String username, List<OrderStatus> executed, LocalDateTime start, LocalDateTime end);
}