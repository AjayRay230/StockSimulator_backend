package org.ajay.stockSimulator.DTOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class DashboardMetricsDTO {

    private String symbol;
    private int quantity;
    private double avgBuyPrice;
    private double currentPrice;
    private double unrealizedPnL;
    private double portfolioValue;
    private long tradesToday;


}
