package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.Repo.StockPriceRepo;
import org.ajay.stockSimulator.model.StockPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StockPriceService {
    @Autowired
    private StockPriceRepo stockPriceRepo;

    public void savePrice(StockPrice stockPrice) {
        stockPriceRepo.save(stockPrice);
    }

    public List<StockPrice> getPricesBySymbolOrderByTimeAsc(String symbol) {
        return stockPriceRepo.findByStocksymbolOrderByTimestampAsc(symbol);
    }
    public StockPrice AddStockWithSymbol(StockPrice stockPrice) {
        stockPriceRepo.save(stockPrice);
        return stockPrice;
    }
}
