package org.ajay.stockSimulator.Repo;

import org.ajay.stockSimulator.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockPriceRepo extends JpaRepository<StockPrice,Long> {
    List<StockPrice> findByStocksymbolOrderByTimestampAsc(String stocksymbol);
}
