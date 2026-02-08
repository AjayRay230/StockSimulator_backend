package org.ajay.stockSimulator.DTOs;

public record AuthResponseDTO(
        String token,
        String firstName,
        String lastName,
        String role
) {}