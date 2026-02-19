package org.ajay.stockSimulator.Repo;

import org.ajay.stockSimulator.model.LimitOrder;
import org.ajay.stockSimulator.model.OrderStatus;
import org.ajay.stockSimulator.model.TransactionType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LimitOrderRepo extends JpaRepository<LimitOrder, Long> {

    List<LimitOrder> findByStatus(OrderStatus status);

    List<LimitOrder> findByStockSymbolAndStatus(
            String stockSymbol,
            OrderStatus status
    );

    List<LimitOrder> findByStockSymbolAndTypeAndStatusOrderByPriceDescCreatedAtAsc(
            String symbol,
            TransactionType type,
            OrderStatus status
    );

    List<LimitOrder> findByStockSymbolAndTypeAndStatusOrderByPriceAscCreatedAtAsc(
            String symbol,
            TransactionType type,
            OrderStatus status
    );

    // FIXED: Added caching to reduce database hits
    @Cacheable(value = "distinctStockSymbols", unless = "#result.isEmpty()")
    @Query("SELECT DISTINCT l.stockSymbol FROM LimitOrder l WHERE l.status = 'PENDING'")
    List<String> findDistinctSymbols();

    long countByUsernameAndStatusInAndCreatedAtBetween(
            String username,
            List<OrderStatus> statuses,
            LocalDateTime start,
            LocalDateTime end
    );
}