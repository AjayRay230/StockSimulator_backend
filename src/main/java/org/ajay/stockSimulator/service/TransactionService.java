package org.ajay.stockSimulator.service;
import jakarta.transaction.Transactional;
import org.ajay.stockSimulator.DTOs.ActiveTraderDTO;
import org.ajay.stockSimulator.DTOs.RecentTradeDTO;
import org.ajay.stockSimulator.DTOs.TopStockDTO;
import org.ajay.stockSimulator.DTOs.TradersExecutedDTO;
import org.ajay.stockSimulator.events.TradePlacedEvent;
import org.ajay.stockSimulator.model.*;
import org.ajay.stockSimulator.Repo.PortfolioItemRepo;
import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.Repo.TransactionRepo;
import org.ajay.stockSimulator.Repo.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    @Autowired
    private ApplicationEventPublisher publisher;

    @Transactional
    public void buyStock(String username,
                         String stocksymbol,
                         Integer quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Stock stock = stockRepo.findById(stocksymbol)
                .orElseThrow(() -> new RuntimeException("No stock found"));

        BigDecimal totalPrice =
                stock.getCurrentprice().multiply(BigDecimal.valueOf(quantity));

        if (user.getAmount().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Optimistic locking protection happens here
        user.setAmount(user.getAmount().subtract(totalPrice));

        PortfolioItem item =
                portfolioItemRepo.findByUser_UserIdAndStocksymbol(
                        user.getUserId(), stocksymbol);

        if (item == null) {
            item = new PortfolioItem();
            item.setUser(user);
            item.setStock(stock);
            item.setStocksymbol(stocksymbol);
            item.setQuantity(quantity);
            item.setAveragebuyprice(stock.getCurrentprice());
        } else {

            int oldQty = item.getQuantity();

            BigDecimal oldTotal =
                    item.getAveragebuyprice()
                            .multiply(BigDecimal.valueOf(oldQty));

            BigDecimal newTotal =
                    stock.getCurrentprice()
                            .multiply(BigDecimal.valueOf(quantity));

            int newQty = oldQty + quantity;

            BigDecimal newAvg =
                    oldTotal.add(newTotal)
                            .divide(BigDecimal.valueOf(newQty),
                                    2,
                                    RoundingMode.HALF_UP);

            item.setQuantity(newQty);
            item.setAveragebuyprice(newAvg);
        }

        portfolioItemRepo.save(item);

        Transaction tnx = new Transaction();
        tnx.setUser(user);
        tnx.setQuantity(quantity);
        tnx.setStocksymbol(stocksymbol);
        tnx.setType(TransactionType.BUY);
        tnx.setTimestamp(LocalDateTime.now());
        tnx.setCurrentprice(stock.getCurrentprice());
        tnx.setTotalAmount(totalPrice);

        transactionRepo.save(tnx);


        publisher.publishEvent(
                new TradePlacedEvent(
                        user.getUserId(),
                        user.getUsername(),
                        stocksymbol,
                        quantity,
                        stock.getCurrentprice().doubleValue(),
                        TransactionType.BUY
                )
        );
    }
    @Transactional
    public void sellStock(Long id, String stocksymbol, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        User user = userRepo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with ID: " + id
                ));

        Stock stock = stockRepo.findById(stocksymbol)
                .orElseThrow(() -> new RuntimeException(
                        "No stock found with symbol: " + stocksymbol
                ));

        PortfolioItem item = portfolioItemRepo
                .findByUser_UserIdAndStocksymbol(id, stocksymbol);

        if (item == null || item.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock to sell");
        }

        BigDecimal totalPrice =
                stock.getCurrentprice().multiply(BigDecimal.valueOf(quantity));

        // Optimistic locking happens here
        user.setAmount(user.getAmount().add(totalPrice));

        int remainingQty = item.getQuantity() - quantity;

        if (remainingQty <= 0) {
            portfolioItemRepo.delete(item);
        } else {
            item.setQuantity(remainingQty);
            portfolioItemRepo.save(item);
        }

        Transaction tnx = new Transaction();
        tnx.setUser(user);
        tnx.setStocksymbol(stocksymbol);
        tnx.setQuantity(quantity);
        tnx.setType(TransactionType.SELL);
        tnx.setTimestamp(LocalDateTime.now());
        tnx.setCurrentprice(stock.getCurrentprice());
        tnx.setTotalAmount(totalPrice);

        transactionRepo.save(tnx);

        publisher.publishEvent(
                new TradePlacedEvent(
                        user.getUserId(),
                        user.getUsername(),
                        stocksymbol,
                        quantity,
                        stock.getCurrentprice().doubleValue(),
                        TransactionType.SELL
                )
        );

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
                        (Long) obj[1]
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Trades Executed Today
    public List<TradersExecutedDTO> getTradesExecutedToday(int limit) {

        return transactionRepo.findTradesExecuted()
                .stream()
                .limit(limit)
                .map(obj -> {

                    String symbol = (String) obj[0];

                    Long totalTrades = ((Number) obj[1]).longValue();

                    java.sql.Timestamp timestamp = (java.sql.Timestamp) obj[2];
                    LocalDateTime time = timestamp.toLocalDateTime();

                    return new TradersExecutedDTO(
                            symbol,
                            totalTrades,
                            time
                    );
                })
                .collect(Collectors.toList());
    }

    public List<ActiveTraderDTO> getActiveTradersToday(int limit) {
        return transactionRepo.findActiveTraders()
                .stream()
                .limit(limit)
                .map(obj -> new ActiveTraderDTO(
                        (Long) obj[0],
                        (String) obj[1],
                        (String) obj[2],
                        (Long) obj[3]
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public void settleMatchedTrade(
            String buyerUsername,
            String sellerUsername,
            String stockSymbol,
            int quantity,
            BigDecimal executionPrice
    ) {

        User buyer = userRepo.findByUsername(buyerUsername);
        User seller = userRepo.findByUsername(sellerUsername);

        if (buyer == null || seller == null) {
            throw new RuntimeException("Buyer or seller not found");
        }

        BigDecimal totalPrice =
                executionPrice.multiply(BigDecimal.valueOf(quantity));

        //  Check buyer funds
        if (buyer.getAmount().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Buyer has insufficient funds");
        }

        //  Deduct buyer
        buyer.setAmount(buyer.getAmount().subtract(totalPrice));

        //  Add seller
        seller.setAmount(seller.getAmount().add(totalPrice));

        //  Update buyer portfolio
        PortfolioItem buyerItem =
                portfolioItemRepo.findByUser_UserIdAndStocksymbol(
                        buyer.getUserId(), stockSymbol);

        if (buyerItem == null) {
            buyerItem = new PortfolioItem();
            buyerItem.setUser(buyer);
            buyerItem.setStocksymbol(stockSymbol);
            buyerItem.setQuantity(quantity);
            buyerItem.setAveragebuyprice(executionPrice);
        } else {

            int oldQty = buyerItem.getQuantity();

            BigDecimal oldTotal =
                    buyerItem.getAveragebuyprice()
                            .multiply(BigDecimal.valueOf(oldQty));

            BigDecimal newTotal =
                    executionPrice.multiply(BigDecimal.valueOf(quantity));

            int newQty = oldQty + quantity;

            BigDecimal newAvg =
                    oldTotal.add(newTotal)
                            .divide(BigDecimal.valueOf(newQty),
                                    2,
                                    RoundingMode.HALF_UP);

            buyerItem.setQuantity(newQty);
            buyerItem.setAveragebuyprice(newAvg);
        }

        portfolioItemRepo.save(buyerItem);

        // Update seller portfolio
        PortfolioItem sellerItem =
                portfolioItemRepo.findByUser_UserIdAndStocksymbol(
                        seller.getUserId(), stockSymbol);

        if (sellerItem == null || sellerItem.getQuantity() < quantity) {
            throw new RuntimeException("Seller insufficient shares");
        }

        int remaining = sellerItem.getQuantity() - quantity;

        if (remaining == 0) {
            portfolioItemRepo.delete(sellerItem);
        } else {
            sellerItem.setQuantity(remaining);
            portfolioItemRepo.save(sellerItem);
        }

        //  Create BUY transaction
        Transaction buyTxn = new Transaction();
        buyTxn.setUser(buyer);
        buyTxn.setStocksymbol(stockSymbol);
        buyTxn.setQuantity(quantity);
        buyTxn.setType(TransactionType.BUY);
        buyTxn.setCurrentprice(executionPrice);
        buyTxn.setTotalAmount(totalPrice);
        buyTxn.setTimestamp(LocalDateTime.now());

        transactionRepo.save(buyTxn);

        //  Create SELL transaction
        Transaction sellTxn = new Transaction();
        sellTxn.setUser(seller);
        sellTxn.setStocksymbol(stockSymbol);
        sellTxn.setQuantity(quantity);
        sellTxn.setType(TransactionType.SELL);
        sellTxn.setCurrentprice(executionPrice);
        sellTxn.setTotalAmount(totalPrice);
        sellTxn.setTimestamp(LocalDateTime.now());

        transactionRepo.save(sellTxn);

        //  Publish events for both sides
        publisher.publishEvent(
                new TradePlacedEvent(
                        buyer.getUserId(),
                        buyer.getUsername(),
                        stockSymbol,
                        quantity,
                        executionPrice.doubleValue(),
                        TransactionType.BUY
                )
        );

        publisher.publishEvent(
                new TradePlacedEvent(
                        seller.getUserId(),
                        seller.getUsername(),
                        stockSymbol,
                        quantity,
                        executionPrice.doubleValue(),
                        TransactionType.SELL
                )
        );
    }

}
