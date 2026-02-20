package org.ajay.stockSimulator.DTOs;


public class DashboardMetricsDTO {

    private String symbol;
    private int quantity;
    private double avgBuyPrice;
    private double currentPrice;
    private double unrealizedPnL;
    private double portfolioValue;
    private long tradesToday;
    private double marketCap;
    public DashboardMetricsDTO(
            String symbol,
            int quantity,
            double avgBuyPrice,
            double currentPrice,
            double unrealizedPnL,
            double portfolioValue,
            long tradesToday,
            double marketCap) {

        this.symbol = symbol;
        this.quantity = quantity;
        this.avgBuyPrice = avgBuyPrice;
        this.currentPrice = currentPrice;
        this.unrealizedPnL = unrealizedPnL;
        this.portfolioValue = portfolioValue;
        this.tradesToday = tradesToday;
        this.marketCap = marketCap;
    }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getAvgBuyPrice() { return avgBuyPrice; }
    public double getCurrentPrice() { return currentPrice; }
    public double getUnrealizedPnL() { return unrealizedPnL; }
    public double getPortfolioValue() { return portfolioValue; }
    public long getTradesToday() { return tradesToday; }
    public double getMarketCap() {
        return marketCap;
    }
}

