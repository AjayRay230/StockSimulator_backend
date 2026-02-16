package org.ajay.stockSimulator.Repo;

import org.ajay.stockSimulator.model.PortfolioItem;
import org.ajay.stockSimulator.model.Stock;
import org.ajay.stockSimulator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioItemRepo extends JpaRepository<PortfolioItem,Long> {

    List<PortfolioItem> findAllByUser_UserId(Long userId);

    @Query("SELECT pi FROM PortfolioItem pi JOIN FETCH pi.stock WHERE pi.user.userId = :userId")
    List<PortfolioItem> findAllByUserIdWithStock(@Param("userId") Long userId);


    PortfolioItem findByUser_UserIdAndStocksymbol(Long userId, String stocksymbol);

    @Query("SELECT SUM(p.quantity * s.currentprice) " +
            "FROM PortfolioItem p " +
            "JOIN p.stock s " +
            "WHERE p.user.userId = :userId")
    Double getUserPortfolioValue(@Param("userId") Long userId);

    List<PortfolioItem> findByUserUserId(Long userId);

    List<PortfolioItem> findByUser(User user);

    @Query("SELECT DISTINCT p.user FROM PortfolioItem p")
    List<User> findDistinctUsersWithPortfolio();
}
