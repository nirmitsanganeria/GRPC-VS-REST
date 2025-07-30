package com.bank.poc.grpcvsrest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CorporateDataService {

    private static final List<TradeEntity> tradeDatabase;

    // This static block runs once when the class is loaded,
    // populating our "database" with 100 trades.
    static {
        List<TradeEntity> trades = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            trades.add(new TradeEntity(
                UUID.randomUUID().toString(),
                "VANGUARD_S&P_500",
                100 + i,
                350.75,
                Instant.now().toString()
            ));
        }
        // Make the list unmodifiable to simulate read-only data
        tradeDatabase = Collections.unmodifiableList(trades);
    }

    /**
     * Simulates fetching the static list of trades from a database.
     * @return A list of 100 trade entities.
     */
    public List<TradeEntity> getTradesFromDatabase() {
        return tradeDatabase;
    }
}