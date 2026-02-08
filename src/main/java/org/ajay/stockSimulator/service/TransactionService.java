package org.ajay.stockSimulator.service;
import org.ajay.stockSimulator.DTOs.ActiveTraderDTO;
import org.ajay.stockSimulator.DTOs.RecentTradeDTO;
import org.ajay.stockSimulator.DTOs.TopStockDTO;
import org.ajay.stockSimulator.DTOs.TradersExecutedDTO;
import org.ajay.stockSimulator.model.*;
import org.ajay.stockSimulator.Repo.PortfolioItemRepo;
import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.Repo.TransactionRepo;
import org.ajay.stockSimulator.Repo.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private StockRepo stockRepo;
    @Autowired
    private PortfolioItemRepo portfolioItemRepo;
    @Autowired
    private UserRepo userRepo;
    public TransactionService(TransactionRepo transactionRepo, StockRepo stockRepo) {
        this.transactionRepo = transactionRepo;
        this.stockRepo = stockRepo;
    }
    public void buyStock(User user,String stocksymbol,Integer quantity)
    {



        Stock stock = stockRepo.findById(stocksymbol)
                .orElseThrow(() -> new RuntimeException("No stock found"));


        BigDecimal totalPrice = stock.getCurrentprice().multiply(BigDecimal.valueOf(quantity));
        if(user.getAmount().compareTo(totalPrice)<=0)
        {
            throw new RuntimeException("Insufficient funds");
        }
        user.setAmount(user.getAmount().subtract(totalPrice));

        userRepo.save(user);
        PortfolioItem item = portfolioItemRepo.findByUser_UserIdAndStocksymbol(user.getUserId(),stocksymbol);
        if(item==null)
        {
            item = new  PortfolioItem();
            item.setUser(user);
            item.setStock(stock);
            item.setStocksymbol(stocksymbol);
            item.setQuantity(quantity);
            item.setAveragebuyprice(stock.getCurrentprice());

        }
        else
        {
            int oldQty = item.getQuantity();
            BigDecimal oldPrice = item.getAveragebuyprice().multiply(BigDecimal.valueOf(oldQty));
            BigDecimal newPrice = stock.getCurrentprice().multiply(BigDecimal.valueOf(quantity));
            int newQty = oldQty + quantity;
            BigDecimal newAvg = oldPrice.add(newPrice).divide(BigDecimal.valueOf(newQty), 2, RoundingMode.HALF_UP);
            item.setQuantity(newQty);
            item.setAveragebuyprice(newAvg);
        }

        portfolioItemRepo.save(item);
        Transaction tnx = new Transaction() ;
        tnx.setUser(user);
        tnx.setQuantity(quantity);
        tnx.setStocksymbol(stocksymbol);
        tnx.setType(TransactionType.BUY);
        tnx.setTimestamp(LocalDateTime.now());
        tnx.setCurrentprice(stock.getCurrentprice());
        tnx.setTotalAmount(totalPrice);
        transactionRepo.save(tnx);




                



    }

    public void sellStock(Long id, String stocksymbol, int quantity) {

        // 0. Basic validation
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        // 1. Get User
        User user = userRepo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with ID: " + id
                ));

        // 2. Get Stock
        Stock stock = stockRepo.findById(stocksymbol)
                .orElseThrow(() -> new RuntimeException(
                        "No stock found with symbol: " + stocksymbol
                ));

        // 3. Get PortfolioItem FIRST (ownership + quantity check)
        PortfolioItem item = portfolioItemRepo
                .findByUser_UserIdAndStocksymbol(id, stocksymbol);

        if (item == null || item.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock to sell");
        }

        // 4. Calculate total sell value
        BigDecimal totalPrice =
                stock.getCurrentprice().multiply(BigDecimal.valueOf(quantity));

        // 5. Update User balance AFTER validation
        user.setAmount(user.getAmount().add(totalPrice));
        userRepo.save(user);

        // 6. Update PortfolioItem
        int remainingQty = item.getQuantity() - quantity;

        if (remainingQty <= 0) {
            portfolioItemRepo.delete(item);
        } else {
            item.setQuantity(remainingQty);
            portfolioItemRepo.save(item);
        }

        // 7. Save Transaction
        Transaction tnx = new Transaction();
        tnx.setUser(user);
        tnx.setStocksymbol(stocksymbol);
        tnx.setQuantity(quantity);
        tnx.setType(TransactionType.SELL);
        tnx.setTimestamp(LocalDateTime.now());
        tnx.setCurrentprice(stock.getCurrentprice());
        tnx.setTotalAmount(totalPrice);

        transactionRepo.save(tnx);
    }



    public List<Transaction> getuserHistory(Long id) {
        return transactionRepo.findByUser_UserId(id);
    }

    public List<RecentTradeDTO> getRecentTransactions(int limit) {
        return transactionRepo.findAllByOrderByTimestampDesc(PageRequest.of(0, limit))
                .getContent()
                .stream()
                .map(t -> new RecentTradeDTO(
                        t.getUser().getUsername(),
                        t.getStocksymbol(),
                        stockRepo.findById(t.getStocksymbol())
                                .map(s -> s.getCompanyname())
                                .orElse("Unknown"),
                        t.getCurrentprice(),
                        t.getQuantity(),
                        t.getType(),
                        t.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    public List<TopStockDTO> getTopStocks(int limit) {
        return transactionRepo.findTopStocks()
                .stream()
                .limit(limit)
                .map(obj -> new TopStockDTO(
                        (String) obj[0],
                        (Long) obj[1]   // ✅ keep as long
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Trades Executed Today
    public List<TradersExecutedDTO> getTradesExecutedToday(int limit) {
        return transactionRepo.findTradesExecuted()
                .stream()
                .limit(limit)
                .map(obj -> new TradersExecutedDTO(
                        (String) obj[0],       // stock symbol
                        (Long) obj[1],         // ✅ totalTrades as long
                        (LocalDateTime) obj[2] // timestamp
                ))
                .collect(Collectors.toList());
    }

    public List<ActiveTraderDTO> getActiveTradersToday(int limit) {
        return transactionRepo.findActiveTraders()
                .stream()
                .limit(limit)
                .map(obj -> new ActiveTraderDTO(
                        (Long) obj[0],     // ✅ userId
                        (String) obj[1],   // userName
                        (String) obj[2],   // email
                        (Long) obj[3]      // tradeCount
                ))
                .collect(Collectors.toList());
    }

}
