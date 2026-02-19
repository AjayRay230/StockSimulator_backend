package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.DTOs.DashboardMetricsDTO;
import org.ajay.stockSimulator.DTOs.PortfolioItemDTO;


import org.ajay.stockSimulator.DTOs.PortfolioItemDtos;
import org.ajay.stockSimulator.DTOs.StockDto;
import org.ajay.stockSimulator.Repo.*;
import org.ajay.stockSimulator.model.OrderStatus;
import org.ajay.stockSimulator.model.PortfolioItem;
import org.ajay.stockSimulator.model.Stock;
import org.ajay.stockSimulator.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service

public class PortfolioItemService {

    @Autowired
    private PortfolioItemRepo portfolioItemRepo;
   @Autowired
   private TransactionRepo transactionRepo;
   @Autowired
   private UserRepo userRepo;
    @Autowired
    private StockRepo stockRepo;
 @Autowired
 private LimitOrderRepo limitOrderRepo;
    public List<PortfolioItemDtos> getAllPortfolioItemById(Long userId) {
        List<PortfolioItem> items = portfolioItemRepo.findAllByUserIdWithStock(userId);

        return items.stream().map(pi -> new PortfolioItemDtos(
                pi.getId(),
                pi.getStocksymbol(),
                pi.getQuantity(),
                pi.getAveragebuyprice(),
                new StockDto(
                        pi.getStock().getSymbol(),
                        pi.getStock().getCompanyname(),
                        pi.getStock().getCurrentprice(),
                        pi.getStock().getChangepercent(),
                        pi.getStock().getLastupdate()
                )
        )).toList();
    }


    public PortfolioItem getPortfolioBySymbol(Long id,String stocksymbol) {
        return portfolioItemRepo.findByUser_UserIdAndStocksymbol(id,stocksymbol);
    }

    public PortfolioItem addNewPortfolioItem(Long id, PortfolioItemDTO dto) {
        // 1. Find user
        User user = userRepo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        // 2. Validate input
        if (dto.getQuantity() <= 0 || dto.getAveragebuyprice() <= 0) {
            throw new RuntimeException("Quantity and AverageBuyPrice should be greater than 0");
        }

        // 3. Find stock
        var stock = stockRepo.findBySymbol(dto.getStocksymbol())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + dto.getStocksymbol()));

        // 4. Create portfolio item
        PortfolioItem item = new PortfolioItem();
        item.setUser(user);
        item.setStock(stock);
        item.setStocksymbol(stock.getSymbol());
        item.setQuantity(dto.getQuantity());
        item.setAveragebuyprice(BigDecimal.valueOf(dto.getAveragebuyprice()));

        // 5. Save
        return portfolioItemRepo.save(item);
    }


    public PortfolioItem addPortfolioItem(PortfolioItem portfolioItem) {
        return portfolioItemRepo.save(portfolioItem);
    }

    public PortfolioItem addPortfolioItem(int quantity, double averageBuyPrice, String stockSymbol) {
        PortfolioItem item = new PortfolioItem();
        item.setQuantity(quantity);
        item.setAveragebuyprice(BigDecimal.valueOf(averageBuyPrice));
        item.setStocksymbol(stockSymbol);
        return portfolioItemRepo.save(item);
    }

    public PortfolioItem deletePortfioItem(Long userId, String stocksymbol) {
        PortfolioItem item = portfolioItemRepo.findByUser_UserIdAndStocksymbol(userId,stocksymbol);
        if(item!=null){
            portfolioItemRepo.delete(item);
        }
        return item;
    }

    public double calculateTotalPortfolioValue(User user) {

        List<PortfolioItem> holdings =
                portfolioItemRepo.findByUser(user);

        double total = 0.0;

        for (PortfolioItem holding : holdings) {

            Stock stock = holding.getStock();

            if (stock == null || stock.getCurrentprice() == null) {
                continue; // skip if no live price yet
            }

            total += holding.getQuantity()
                    * stock.getCurrentprice().doubleValue();
        }

        return total;
    }

    public DashboardMetricsDTO getDashboardMetrics(User user, String symbol) {

        PortfolioItem portfolio =
                portfolioItemRepo
                        .findByUserAndStocksymbol(user, symbol)
                        .orElse(null);

        int quantity = 0;
        BigDecimal avgBuyPrice = BigDecimal.ZERO;

        if (portfolio != null) {
            quantity = portfolio.getQuantity();
            avgBuyPrice = portfolio.getAveragebuyprice();
        }

        Stock stock = stockRepo.findById(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        BigDecimal currentPrice =
                stock.getCurrentprice() != null
                        ? stock.getCurrentprice()
                        : BigDecimal.ZERO;

        BigDecimal unrealizedPnL = BigDecimal.ZERO;

        if (quantity > 0) {
            unrealizedPnL = currentPrice
                    .subtract(avgBuyPrice)
                    .multiply(BigDecimal.valueOf(quantity));
        }

        double totalPortfolioValue =
                calculateTotalPortfolioValue(user);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        long tradesToday =
                limitOrderRepo
                        .countByUsernameAndStatusInAndCreatedAtBetween(
                                user.getUsername(),
                                List.of(OrderStatus.EXECUTED, OrderStatus.PARTIAL),
                                start,
                                end
                        );

        return new DashboardMetricsDTO(
                symbol,
                quantity,
                avgBuyPrice.doubleValue(),
                currentPrice.doubleValue(),
                unrealizedPnL.doubleValue(),
                totalPortfolioValue,
                tradesToday
        );
    }

    public List<User> findUsersWithPortfolio() {
        return portfolioItemRepo.findDistinctUsersWithPortfolio();
    }

}
