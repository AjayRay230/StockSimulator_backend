package org.ajay.stockSimulator;

import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.Stock;
import org.ajay.stockSimulator.model.User;
import org.ajay.stockSimulator.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.*;
@SpringBootTest
class TradeConcurrencyTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StockRepo stockRepo;

    @Test
    void concurrentBuyTest() throws InterruptedException {

        // Ensure test user exists
        User user = userRepo.findByUsername("testuser");

        if (user == null) {
            user = new User();
            user.setUsername("testuser");
            user.setAmount(BigDecimal.valueOf(1000000.0));
            userRepo.save(user);
        }

// Ensure stock exists
        Stock stock = stockRepo.findBySymbol("AAPL")
                .orElseGet(() -> {
                    Stock s = new Stock();
                    s.setSymbol("AAPL");
                    s.setCompanyname("Apple");
                    return stockRepo.save(s);
                });

        int threads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    transactionService.buyStock("testuser", "AAPL", 1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }
}
