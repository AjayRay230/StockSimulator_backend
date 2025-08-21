package org.ajay.stockSimulator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to User
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relation to Stock via stock.symbol

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stocksymbol", referencedColumnName = "symbol", insertable = false, updatable = false)
    private Stock stock;


    // Denormalized copy of the stock symbol
    @Column(name = "stocksymbol", nullable = false, length = 10)
    private String stocksymbol;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal averagebuyprice;
}
