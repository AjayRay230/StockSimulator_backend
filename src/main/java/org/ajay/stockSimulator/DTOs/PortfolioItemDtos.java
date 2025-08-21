package org.ajay.stockSimulator.DTOs;

// PortfolioItemDto.java
import java.math.BigDecimal;

public record PortfolioItemDtos(
        Long id,
        String stocksymbol,
        int quantity,
        BigDecimal averagebuyprice,
        StockDto stock
) {}
