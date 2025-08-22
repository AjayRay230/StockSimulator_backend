package org.ajay.stockSimulator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor

@NoArgsConstructor
public class StockPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "symbol")
    private String stocksymbol;
    private double openPrice;
    private double closePrice;
    private double highPrice;
    private double lowPrice;
    private LocalDateTime timestamp;


}
