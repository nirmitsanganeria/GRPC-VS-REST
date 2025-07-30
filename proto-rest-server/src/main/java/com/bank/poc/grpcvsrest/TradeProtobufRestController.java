package com.bank.poc.grpcvsrest;

// Add these required imports
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.bank.poc.grpcvsrest.grpc.Trade;
import com.bank.poc.grpcvsrest.grpc.TradeResponse;

@RestController
public class TradeProtobufRestController {

    private final CorporateDataService corporateDataService;

    public TradeProtobufRestController(CorporateDataService corporateDataService) {
        this.corporateDataService = corporateDataService;
    }

    @GetMapping(value = "/api/proto/clients/{clientId}/trades", produces = "application/x-protobuf")
    public TradeResponse getTradesForClient(@PathVariable String clientId) {
        List<TradeEntity> entities = corporateDataService.getTradesFromDatabase();

        List<Trade> grpcTrades = entities.stream()
            .map(entity -> Trade.newBuilder()
                .setTradeId(entity.tradeId)
                .setInstrumentName(entity.instrumentName)
                .setQuantity(entity.quantity)
                .setPrice(entity.price)
                .setTradeTimestamp(entity.tradeTimestamp)
                .build())
            .collect(Collectors.toList());

        return TradeResponse.newBuilder().addAllTrades(grpcTrades).build();
    }
}