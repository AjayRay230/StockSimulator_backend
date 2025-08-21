package org.ajay.stockSimulator.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {
    private String token;
    private Long userId;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
}
