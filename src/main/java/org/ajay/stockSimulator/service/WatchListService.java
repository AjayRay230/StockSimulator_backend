package org.ajay.stockSimulator.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.Repo.WatchListRepo;
import org.ajay.stockSimulator.model.User;
import org.ajay.stockSimulator.model.Watchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class WatchListService {
    @Autowired
    private WatchListRepo watchListRepo;
  @PersistenceContext
  private EntityManager em;
    public void addToWatchlist(String stocksymbol, Long id) {
        Optional<Watchlist> list = watchListRepo.findByUser_UserIdAndStocksymbol(id, stocksymbol);
        if(list.isPresent()) {
            throw new RuntimeException("Watchlist already exists");
        }
        Watchlist watchlist = new Watchlist();
       User user = em.find(User.class, id);
        user.setUserId(id);
        watchlist.setStocksymbol(stocksymbol);
        watchlist.setUser(user);
        watchlist.setAddTime(LocalDateTime.now());
        watchListRepo.save(watchlist);
    }
    @Transactional
    public void removeFromWatchlist(Long id, String symbol) {
        watchListRepo.deleteByUser_UserIdAndStocksymbol(id,symbol);
    }

    public List<Watchlist> getUserWatchlist(Long id) {
        return watchListRepo.findByUser_UserId((id));
    }
}
