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
//    @Query("SELECT t.stocksymbol, COUNT(t) as tradeCount " +
//            "FROM Transaction t " +
//            "GROUP BY t.stocksymbol " +
//            "ORDER BY tradeCount DESC")
//    List<Object[]> findTopStocks();



//    @Query(value = "SELECT * FROM transactions ORDER BY `timestamp` DESC LIMIT :limit", nativeQuery = true)
//    List<Transaction> findAllByOrderByTimestampDesc(Pageable pageable);
//
//    // Top traded stocks by transaction count
//    @Query(value = "SELECT stock_symbol, COUNT(*) as trade_count " +
//            "FROM transactions GROUP BY stock_symbol " +
//            "ORDER BY trade_count DESC LIMIT :limit", nativeQuery = true)
//    List<Object[]> findTopTradedStocks(@Param("limit") int limit);
//    @Query("SELECT COUNT(DISTINCT t.user.userId) FROM Transaction t WHERE t.timestamp BETWEEN :start AND :end")
//    long countDistinctUsersByTimestampBetween(LocalDateTime start, LocalDateTime end);

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
    @Query("SELECT t.stocksymbol, COUNT(t), MAX(t.timestamp) FROM Transaction t " +
            "WHERE DATE(t.timestamp) = CURRENT_DATE " +
            "GROUP BY t.stocksymbol ORDER BY COUNT(t) DESC")
    List<Object[]> findTradesExecuted();

    // Active Traders
    @Query("SELECT u.userId, u.username, u.email, COUNT(t) " +
            "FROM Transaction t JOIN t.user u " +
            "WHERE DATE(t.timestamp) = CURRENT_DATE " +
            "GROUP BY u.userId, u.username, u.email " +
            "ORDER BY COUNT(t) DESC")
    List<Object[]> findActiveTraders();

    Page<Transaction> findAllByOrderByTimestampDesc(Pageable pageable);

//    interface UserRepository extends org.springframework.data.repository.Repository<User, Long> {
//
//
//
//
//       List<Transaction> findByUserId(Long userId);


}
