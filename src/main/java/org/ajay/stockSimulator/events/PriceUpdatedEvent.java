package org.ajay.stockSimulator.events;



import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class PriceUpdatedEvent {

    private final Map<String, Double> updatedPrices;

    public PriceUpdatedEvent(Map<String, Double> updatedPrices) {
        this.updatedPrices = updatedPrices;
    }


}