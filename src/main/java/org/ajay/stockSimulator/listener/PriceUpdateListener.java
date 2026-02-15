package org.ajay.stockSimulator.listener;

import org.ajay.stockSimulator.events.PriceUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PriceUpdateListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handlePriceUpdate(PriceUpdatedEvent event) {

        messagingTemplate.convertAndSend(
                "/topic/prices",
                event.getUpdatedSymbols()
        );
    }
}