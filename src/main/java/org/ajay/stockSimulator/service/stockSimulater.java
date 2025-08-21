package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.model.Stock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class stockSimulater {
    private StockRepo stockRepo;
    private Random random =  new Random();
    public void simulatePriceChange()
    {
        List<Stock> stocks = stockRepo.findAll();
        for(Stock stock : stocks)
        {
            BigDecimal oldPrice = stock.getCurrentprice();
            BigDecimal percentChange = BigDecimal.valueOf((random.nextDouble() - 0.5) * 2); // -1% to +1%
            BigDecimal change = oldPrice.multiply(percentChange).divide(BigDecimal.valueOf(100));
            BigDecimal newPrice = oldPrice.add(change).setScale(2, RoundingMode.HALF_UP);

            stock.setCurrentprice(newPrice);
            stock.setChangepercent(percentChange.setScale(2, RoundingMode.HALF_UP));
            stock.setLastupdate(LocalDateTime.now());

            stockRepo.save(stock);
        }

    }
}
