package com.shop.orderservice.client;

import com.shop.orderservice.dto.InventoryResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * Declarative HTTP client for inventory-service - Spring's built-in equivalent
 * of a Feign client, and the officially recommended replacement for it.
 * <p>
 * There is no implementation of this interface anywhere. Spring generates a
 * proxy at runtime (see RestClientConfig) that turns a method call into an
 * HTTP request and the response body back into objects.
 * <p>
 * Note this interface says nothing about WHERE inventory-service lives - the
 * base URL is configuration, not code. That is what will let Eureka swap the
 * hardcoded localhost:8082 for service discovery later without touching this.
 */
@HttpExchange("/api/inventory")
public interface InventoryClient {

    /**
     * GET /api/inventory?skuCode=iphone_13&skuCode=airpods
     * <p>
     * One request for the whole order rather than one per line item: N HTTP
     * round trips would multiply latency and failure probability by N.
     * <p>
     * Careful: a SKU that does not exist in inventory is simply ABSENT from the
     * response rather than returned as false, so the result can be shorter than
     * the input. The caller has to check for that.
     */
    @GetExchange
    List<InventoryResponse> isInStock(@RequestParam List<String> skuCode);
}
