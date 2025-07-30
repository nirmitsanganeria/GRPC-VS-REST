package com.bank.poc.grpcvsrest;

/**
 * A Plain Old Java Object (POJO) for REST/JSON serialization.
 * Spring will use this class to automatically convert our data to JSON.
 */
public class TradePojo {
    private String tradeId;
    private String instrumentName;
    private long quantity;
    private double price;
    private String tradeTimestamp;

    // A constructor to easily create new instances
    public TradePojo(String tradeId, String instrumentName, long quantity, double price, String tradeTimestamp) {
        this.tradeId = tradeId;
        this.instrumentName = instrumentName;
        this.quantity = quantity;
        this.price = price;
        this.tradeTimestamp = tradeTimestamp;
    }

    // Getter methods so the JSON converter can read the values
    public String getTradeId() { return tradeId; }
    public String getInstrumentName() { return instrumentName; }
    public long getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getTradeTimestamp() { return tradeTimestamp; }
}