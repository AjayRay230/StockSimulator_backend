package org.ajay.stockSimulator.DTOs;




public record ActiveTraderDTO(
        Long userId,
        String username,
        String email,
        long tradeCount
) {}
