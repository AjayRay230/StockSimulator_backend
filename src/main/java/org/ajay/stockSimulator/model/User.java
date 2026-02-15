package org.ajay.stockSimulator.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long userId;
    @Column(name = "firstname")
    private String firstName;
    @Column(name  = "lastname")
    private String lastName;
   private String username;
   private String password;
   private String email;
   private BigDecimal amount;
   private String role;
    @Version
    private Long version;

}
