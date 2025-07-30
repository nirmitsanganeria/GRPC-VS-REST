package com.bank.poc.grpcvsrest;

/**
 * Represents a trade object as it would be mapped from MongoDB
 * by a tool like Morphia. This is our "database entity".
 */
public class TradeEntity {
    // Using public fields for simplicity in this example
    public String tradeId;
    public String instrumentName;
    public long quantity;
    public double price;
    public String tradeTimestamp;

    public TradeEntity(String tradeId, String instrumentName, long quantity, double price, String tradeTimestamp) {
        this.tradeId = tradeId;
        this.instrumentName = instrumentName;
        this.quantity = quantity;
        this.price = price;
        this.tradeTimestamp = tradeTimestamp;
    }
}