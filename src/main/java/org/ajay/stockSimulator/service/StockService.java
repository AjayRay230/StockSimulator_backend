package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.model.Stock;
import org.ajay.stockSimulator.model.StockPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private StockRepo stockRepo;
    public List<Stock> getAllStocksWithPrice(BigDecimal currentprice) {
       return  stockRepo.findByCurrentprice(currentprice);
    }

    public Stock getStockWithSymbol(String symbol) {
        return stockRepo.findById(String.valueOf(symbol)).orElseThrow(()-> new RuntimeException("Stock not found" + symbol));

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
        return stockRepo.searchStockLike(query);
    }

    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }

    public Stock findStockBySymbolOrCompanyName(String query) {
        return stockRepo
                .findBySymbolIgnoreCase(query)
                .orElse(
                        stockRepo.findByCompanynameContainingIgnoreCase(query)
                                .orElse(null)
                );
    }
}
