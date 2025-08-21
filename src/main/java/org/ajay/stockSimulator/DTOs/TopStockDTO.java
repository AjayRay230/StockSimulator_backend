package org.ajay.stockSimulator.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopStockDTO {
private String stockSymbol;
private long tradeCount;
}
