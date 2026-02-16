package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.DTOs.PortfolioItemDTO;


import org.ajay.stockSimulator.DTOs.PortfolioItemDtos;
import org.ajay.stockSimulator.DTOs.StockDto;
import org.ajay.stockSimulator.Repo.PortfolioItemRepo;
import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.Repo.TransactionRepo;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.PortfolioItem;
import org.ajay.stockSimulator.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
            total += holding.getQuantity()
                    * holding.getStock().getCurrentprice().doubleValue();
        }

        return total;
    }

    public List<User> findUsersWithPortfolio() {
        return portfolioItemRepo.findDistinctUsersWithPortfolio();
    }
}
