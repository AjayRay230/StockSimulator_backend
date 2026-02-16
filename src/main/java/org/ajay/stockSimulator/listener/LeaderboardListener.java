package org.ajay.stockSimulator.listener;


import org.ajay.stockSimulator.events.TradePlacedEvent;
import org.ajay.stockSimulator.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LeaderboardListener {

    private final UserService userService;

    public LeaderboardListener(UserService userService) {
        this.userService = userService;
    }

    @TransactionalEventListener
    public void handleTradePlacedEvent(TradePlacedEvent event) {


        userService.incrementTradeCount(event.getUserId());

        // You can later add:
        // - analytics update
        // - notification trigger
        // - cache invalidation
    }
}