package org.ajay.stockSimulator.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ajay.stockSimulator.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecentTradeDTO {
    private String username;
    private String stockSymbol;
    private String companyName;
    private BigDecimal currentPrice;
    private int quantity;
    private TransactionType type;
    private LocalDateTime timestamp;
}
