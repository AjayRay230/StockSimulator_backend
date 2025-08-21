package org.ajay.stockSimulator.DTOs;// dto/StockDto.java


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockDto(
        String symbol,
        String companyname,
        BigDecimal currentprice,
        BigDecimal changepercent,
        LocalDateTime lastupdate
) {}
