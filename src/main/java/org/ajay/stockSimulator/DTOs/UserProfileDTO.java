package org.ajay.stockSimulator.DTOs;

import java.math.BigDecimal;

public record UserProfileDTO(
        String firstName,
        String lastName,
        String email,
        BigDecimal amount
) {}
