package org.ajay.stockSimulator.Repo;

import org.ajay.stockSimulator.model.Transaction;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Repository;



import java.util.List;


@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {


    List<Transaction> findByUser_UserId(Long id);

    @Query("SELECT t FROM Transaction t ORDER BY t.timestamp DESC")
    List<Transaction> findTopByOrderByTimestampDesc(Pageable pageable);
    default List<Transaction> findTopByOrderByTimestampDesc(int limit) {
        return findTopByOrderByTimestampDesc((Pageable) PageRequest.of(0, limit));
    }

    // Top Stocks
    @Query("SELECT t.stocksymbol, COUNT(t) FROM Transaction t GROUP BY t.stocksymbol ORDER BY COUNT(t) DESC")
    List<Object[]> findTopStocks();

    // Trades Executed Today
    @Query(value = """
    SELECT stocksymbol, COUNT(id) AS trade_count, MAX(timestamp) AS last_trade
    FROM transactions
    WHERE timestamp >= CURRENT_DATE
      AND timestamp < CURRENT_DATE + INTERVAL '1 day'
    GROUP BY stocksymbol
    ORDER BY trade_count DESC
""", nativeQuery = true)
    List<Object[]> findTradesExecuted();


    // Active Traders
    @Query(value = """
    SELECT u.user_id, u.username, u.email, COUNT(t.id) AS trade_count
    FROM transactions t
    JOIN users u ON t.user_id = u.user_id
    WHERE t.timestamp::date = CURRENT_DATE
    GROUP BY u.user_id, u.username, u.email
    ORDER BY trade_count DESC
""", nativeQuery = true)
    List<Object[]> findActiveTraders();

    Page<Transaction> findAllByOrderByTimestampDesc(Pageable pageable);




}
