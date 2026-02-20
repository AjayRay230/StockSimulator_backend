package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.model.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@EnableScheduling
public class MarketUpdateScheduler {

    @Autowired
    private StockRepo stockRepo;

    @Autowired
    private TwelveDataService twelveDataService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private int rotationIndex = 0;

    private static final int BATCH_SIZE = 5; // safe limit

    @Scheduled(fixedRate = 900000) // 10 minutes
    public void updateMarketPrices() {

        List<Stock> stocks = stockRepo.findAll();

        if (stocks.isEmpty()) return;

        int start = rotationIndex;
        int end = Math.min(start + BATCH_SIZE, stocks.size());

        List<Stock> batch = stocks.subList(start, end);

        for (Stock stock : batch) {

            BigDecimal price =
                    twelveDataService.fetchLivePrice(stock.getSymbol());

            if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {

                stock.setCurrentprice(price);
                stock.setLastupdate(LocalDateTime.now());
                stockRepo.save(stock);

                messagingTemplate.convertAndSend(
                        "/topic/price-updates",
                        Map.of(
                                "symbol", stock.getSymbol(),
                                "price", price,
                                "change", 0,
                                "percentChange", 0
                        )
                );
            }
        }

        rotationIndex = end >= stocks.size() ? 0 : end;

        System.out.println("Updated batch: " + start + " → " + end);
    }

    @Scheduled(cron = "0 0 3 * * ?") // 3 AM daily
    public void updateFundamentals() {

        List<Stock> stocks = stockRepo.findAll();

        for (Stock stock : stocks) {

            BigDecimal shares =
                    twelveDataService.fetchSharesOutstanding(stock.getSymbol());

            if (shares != null && shares.compareTo(BigDecimal.ZERO) > 0) {
                stock.setSharesOutstanding(shares);
                stockRepo.save(stock);
            }
        }

        System.out.println("Fundamentals updated.");
    }
}
