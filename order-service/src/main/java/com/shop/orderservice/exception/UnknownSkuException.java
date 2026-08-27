package com.shop.orderservice.exception;

import lombok.Getter;

import java.util.List;

/**
 * The customer asked for a SKU inventory-service has never heard of.
 * <p>
 * Distinct from OutOfStockException: "we have none right now" is a temporary
 * business answer, while "no such product" means the request itself is wrong.
 * Different cause, different fix, so a different status - 400 Bad Request.
 * <p>
 * This case exists because inventory-service OMITS unknown SKUs from its
 * response rather than returning them with inStock=false. Without an explicit
 * check, an order for a nonexistent product would silently succeed - the
 * "are they all in stock?" test passes trivially when the item is not there.
 */
@Getter
public class UnknownSkuException extends RuntimeException {

    private final List<String> skuCodes;

    public UnknownSkuException(List<String> skuCodes) {
        super("Unknown SKU: " + String.join(", ", skuCodes));
        this.skuCodes = skuCodes;
    }
}
