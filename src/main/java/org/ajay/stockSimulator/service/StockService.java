package org.ajay.stockSimulator.service;

import jakarta.transaction.Transactional;
import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.model.Stock;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
;

@Service
public class StockService {

    @Autowired
    private StockRepo stockRepo;

    @Autowired
    private TwelveDataService twelveDataService;
    public List<Stock> getAllStocksWithPrice(BigDecimal currentprice) {
       return  stockRepo.findByCurrentprice(currentprice);
    }

    public Stock getStockWithSymbol(String symbol) {
        return stockRepo.findById(symbol.toUpperCase()).orElseThrow(()-> new RuntimeException("Stock not found" + symbol));

    }

    public void simulatePrice() {
        //simulate by random price change plus_minus 10%;
        List<Stock> stocks = stockRepo.findAll();
        for (Stock stock : stocks) {
            BigDecimal currentPrice = stock.getCurrentprice();
            double factor = 0.9 + Math.random()*0.2 ;//0.9 to 1.1
            BigDecimal newPrice = currentPrice.multiply(BigDecimal.valueOf(factor));
            stock.setCurrentprice(newPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        stockRepo.saveAll(stocks);

    }





    @Transactional
    public List<Stock> SearchStock(String query) {

        String normalized = query.trim();

        // 1️⃣ Search DB first
        List<Stock> bySymbol =
                stockRepo.findBySymbolContainingIgnoreCase(normalized);

        List<Stock> byName =
                stockRepo.findByCompanynameContainingIgnoreCase(normalized);

        Set<Stock> combined = new LinkedHashSet<>();
        combined.addAll(bySymbol);
        combined.addAll(byName);

        // If DB already has enough results → return
        if (combined.size() >= 5) {
            return new ArrayList<>(combined);
        }

        // 2️⃣ Fetch from TwelveData
        List<Stock> external =
                twelveDataService.fetchSuggestionsFromTwelve(normalized);

        if (external == null || external.isEmpty()) {
            return new ArrayList<>(combined);
        }

        for (Stock stock : external) {

            // Avoid duplicate insert
            if (!stockRepo.existsById(stock.getSymbol())) {
                stockRepo.save(stock);
            }

            combined.add(stock);
        }

        return new ArrayList<>(combined);
    }

    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }
    public Stock findStockBySymbolOrCompanyName(String query) {

        List<Stock> results =
                stockRepo.searchStockLike(query.trim(), PageRequest.of(0, 10));

        return results.stream()
                .sorted((a, b) -> {
                    if (a.getSymbol().equalsIgnoreCase(query)) return -1;
                    if (b.getSymbol().equalsIgnoreCase(query)) return 1;
                    return 0;
                })
                .findFirst()
                .orElse(null);
    }
}
