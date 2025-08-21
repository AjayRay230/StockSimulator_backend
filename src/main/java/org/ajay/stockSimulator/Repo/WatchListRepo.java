package org.ajay.stockSimulator.Repo;

import org.ajay.stockSimulator.model.Watchlist;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchListRepo extends JpaRepository<Watchlist,Long> {
    void deleteByUser_UserIdAndStocksymbol(Long UserId, String stocksymbol);

    Optional<Watchlist> findByUser_UserIdAndStocksymbol(Long userId, String stocksymbol);


    List<Watchlist> findByUser_UserId(Long UserId);
}
