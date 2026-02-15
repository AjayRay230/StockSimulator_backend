package org.ajay.stockSimulator.events;

import java.util.List;

public class PriceUpdatedEvent {

    private final List<String> updatedSymbols;

    public PriceUpdatedEvent(List<String> updatedSymbols) {
        this.updatedSymbols = updatedSymbols;
    }

    public List<String> getUpdatedSymbols() {
        return updatedSymbols;
    }
}