package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.model.Stock;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
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





    public List<Stock> SearchStock(String query) {

        query = query.trim();

        List<Stock> localResults =
                stockRepo.searchStockLike(query, PageRequest.of(0, 10));

        if (!localResults.isEmpty()) {
            return localResults;
        }

        try {
            Stock externalStock = twelveDataService.fetchFromTwelve(query);

            if (externalStock != null) {

                externalStock.setSymbol(
                        externalStock.getSymbol().toUpperCase()
                );

                if (!stockRepo.existsById(externalStock.getSymbol())) {
                    stockRepo.save(externalStock);
                }

                return List.of(externalStock);
            }

        } catch (Exception e) {
            // optional logging
        }

        return List.of();
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
