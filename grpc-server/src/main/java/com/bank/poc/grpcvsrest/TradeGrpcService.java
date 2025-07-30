package com.bank.poc.grpcvsrest;

import java.util.List;
import java.util.stream.Collectors;

import com.bank.poc.grpcvsrest.grpc.Trade;
import com.bank.poc.grpcvsrest.grpc.TradeRequest;
import com.bank.poc.grpcvsrest.grpc.TradeResponse;
import com.bank.poc.grpcvsrest.grpc.TradeServiceGrpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * This is the implementation of the gRPC service defined in our .proto file.
 * The @GrpcService annotation marks it as a gRPC endpoint that Spring should run.
 */
@GrpcService
public class TradeGrpcService extends TradeServiceGrpc.TradeServiceImplBase {

    private final CorporateDataService corporateDataService;

    // Using constructor injection to get an instance of our data service.
    public TradeGrpcService(CorporateDataService corporateDataService) {
        this.corporateDataService = corporateDataService;
    }

    /**
     * This method implements the 'GetTradesForClient' RPC call.
     */
    @Override
    public void getTradesForClient(TradeRequest request, StreamObserver<TradeResponse> responseObserver) {
        // 1. Get the raw "entity" data from the service
        List<TradeEntity> entities = corporateDataService.getTradesFromDatabase();

        // 2. Map the entities to the gRPC-specific Trade format
        List<Trade> grpcTrades = entities.stream()
            .map(entity -> Trade.newBuilder()
                .setTradeId(entity.tradeId)
                .setInstrumentName(entity.instrumentName)
                .setQuantity(entity.quantity)
                .setPrice(entity.price)
                .setTradeTimestamp(entity.tradeTimestamp)
                .build())
            .collect(Collectors.toList());

        // 3. Build and send the response
        TradeResponse response = TradeResponse.newBuilder()
            .addAllTrades(grpcTrades)
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}