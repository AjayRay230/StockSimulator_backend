package org.ajay.stockSimulator.service;

import jakarta.transaction.Transactional;
import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.events.PriceUpdatedEvent;
import org.ajay.stockSimulator.model.Stock;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
;

@Service
public class StockService {

    @Autowired
    private StockRepo stockRepo;

    @Autowired
    private TwelveDataService twelveDataService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    public List<Stock> getAllStocksWithPrice(BigDecimal currentprice) {
       return  stockRepo.findByCurrentprice(currentprice);
    }

    @Cacheable(value = "stocks", key = "#symbol.toUpperCase()")
    public Stock getStockWithSymbol(String symbol) {
        return stockRepo.findById(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found " + symbol));
    }
    @Transactional
    @CacheEvict(value = {"stocks", "stockSearch", "suggestions"}, allEntries = true)
    public void simulatePrice() {

        List<Stock> stocks = stockRepo.findAll();
        Map<String, Double> updatedPrices = new HashMap<>();

        for (Stock stock : stocks) {

            BigDecimal currentPrice = stock.getCurrentprice();
            double factor = 0.9 + Math.random() * 0.2;

            BigDecimal newPrice = currentPrice
                    .multiply(BigDecimal.valueOf(factor))
                    .setScale(2, RoundingMode.HALF_UP);

            stock.setCurrentprice(newPrice);

            //  Store symbol + price
            updatedPrices.put(
                    stock.getSymbol(),
                    newPrice.doubleValue()
            );
        }

        stockRepo.saveAll(stocks);

        //  Publish event with prices
        eventPublisher.publishEvent(
                new PriceUpdatedEvent(updatedPrices)
        );
    }


    @Cacheable(value = "suggestions", key = "#query.trim().toLowerCase()")
    public List<Stock> SearchStock(String query) {
        System.out.println("EXECUTING DB LOGIC - SearchStock");
        String normalized = query.trim();


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


        List<Stock> external =
                twelveDataService.fetchSuggestionsFromTwelve(normalized);

        if (external == null || external.isEmpty()) {
            return new ArrayList<>(combined);
        }

        for (Stock stock : external) {

            stock.setSymbol(stock.getSymbol().toUpperCase());

            try {
                stockRepo.save(stock);
            } catch (DataIntegrityViolationException ignored) {
                // already inserted by another request
            }

            combined.add(stock);
        }
        return new ArrayList<>(combined);
    }

    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }
    @Cacheable(value = "stockSearch", key = "#query.trim().toLowerCase()")
    public Stock findStockBySymbolOrCompanyName(String query) {
        System.out.println("EXECUTING DB LOGIC - SearchStock");
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
