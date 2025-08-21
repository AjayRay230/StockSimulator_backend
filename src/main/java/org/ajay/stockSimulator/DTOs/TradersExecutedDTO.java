package org.ajay.stockSimulator.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradersExecutedDTO {
    private String stockSymbol;

    private long totalTrades;
    private LocalDateTime timestamp;

}
