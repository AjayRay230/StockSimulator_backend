package org.ajay.stockSimulator.events;

import lombok.Getter;

@Getter
public class PortfolioUpdatedEvent {

    private final String username;
    private final Double totalValue;

    public PortfolioUpdatedEvent(String username, Double totalValue) {
        this.username = username;
        this.totalValue = totalValue;
    }
}