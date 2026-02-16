package org.ajay.stockSimulator.listener;

import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.events.PriceUpdatedEvent;
import org.ajay.stockSimulator.model.User;
import org.ajay.stockSimulator.service.PortfolioItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PriceUpdateListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PortfolioItemService portfolioService;

    @Autowired
    private UserRepo userRepository;

    @EventListener
    public void handlePriceUpdate(PriceUpdatedEvent event) {

        // 1️⃣ Broadcast market prices
        messagingTemplate.convertAndSend(
                "/topic/prices",
                event.getUpdatedPrices()
        );

        // 2️⃣ Only recalc users who have holdings
        List<User> usersWithHoldings =
                portfolioService.findUsersWithPortfolio();

        for (User user : usersWithHoldings) {

            double totalValue =
                    portfolioService.calculateTotalPortfolioValue(user);

            messagingTemplate.convertAndSend(
                    "/topic/portfolio/" + user.getUsername(),
                    totalValue
            );
        }
    }
}