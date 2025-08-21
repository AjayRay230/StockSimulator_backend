package org.ajay.stockSimulator.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationRequest {
 private String firstName;
 private String lastName;

private String username;
private String password;
private String email;
private BigDecimal amount;
private String role;
}
