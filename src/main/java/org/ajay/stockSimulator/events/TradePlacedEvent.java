package org.ajay.stockSimulator.events;

import org.ajay.stockSimulator.model.TransactionType;

public class TradePlacedEvent {

    private final Long userId;
    private final String username;
    private final String symbol;
    private final Integer quantity;
    private final double price;
    private final TransactionType type;

    public TradePlacedEvent(Long userId,
                            String username,
                            String symbol,
                            Integer quantity,
                            double price,
                            TransactionType type) {
        this.userId = userId;
        this.username = username;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getSymbol() { return symbol; }
    public Integer getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public TransactionType getType() { return type; }
}