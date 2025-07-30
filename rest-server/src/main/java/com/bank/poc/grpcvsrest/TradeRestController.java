package com.bank.poc.grpcvsrest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a standard REST controller.
 * The @RestController annotation tells Spring to handle web requests with this class.
 */
@RestController
public class TradeRestController {

    private final CorporateDataService corporateDataService;

    // We use the same dependency injection to get our shared data service.
    public TradeRestController(CorporateDataService corporateDataService) {
        this.corporateDataService = corporateDataService;
    }

    /**
     * This method handles HTTP GET requests to the specified URL.
     * @PathVariable maps the '{clientId}' part of the URL to the method's parameter.
     */
    @GetMapping("/api/rest/clients/{clientId}/trades")
    public List<TradePojo> getTradesForClient(@PathVariable String clientId) {
        // 1. Get the raw "entity" data from the service
        List<TradeEntity> entities = corporateDataService.getTradesFromDatabase();

        // 2. Map the entities to the REST-specific POJO format
        return entities.stream()
            .map(entity -> new TradePojo(
                entity.tradeId,
                entity.instrumentName,
                entity.quantity,
                entity.price,
                entity.tradeTimestamp
            ))
            .collect(Collectors.toList());
    }
}