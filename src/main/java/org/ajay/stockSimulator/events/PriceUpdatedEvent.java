package org.ajay.stockSimulator.events;

import lombok.Getter;

import java.util.List;

@Getter
public class PriceUpdatedEvent {

    private final List<String> updatedSymbols;

    public PriceUpdatedEvent(List<String> updatedSymbols) {
        this.updatedSymbols = updatedSymbols;
    }

}