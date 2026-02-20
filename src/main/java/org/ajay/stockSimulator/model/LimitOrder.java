package org.ajay.stockSimulator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class LimitOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String stockSymbol;

    private Integer quantity; // original quantity

    private Integer remainingQuantity;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // BUY or SELL

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PENDING, PARTIAL, EXECUTED, CANCELLED

    private LocalDateTime createdAt;

    @Version
    private Long version;


}