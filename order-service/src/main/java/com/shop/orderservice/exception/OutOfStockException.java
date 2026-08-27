package com.shop.orderservice.exception;

import lombok.Getter;

import java.util.List;

/**
 * The customer asked for something inventory-service says it does not have.
 * <p>
 * This is a legitimate business outcome, not a system fault - the request was
 * well formed and everything worked; the answer is just "no". It maps to
 * 409 Conflict.
 */
@Getter
public class OutOfStockException extends RuntimeException {

    private final List<String> skuCodes;

    public OutOfStockException(List<String> skuCodes) {
        super("Not in stock: " + String.join(", ", skuCodes));
        this.skuCodes = skuCodes;
    }
}
