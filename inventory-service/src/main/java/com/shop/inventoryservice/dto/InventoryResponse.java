package com.shop.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private String skuCode;

    // Deliberately NOT the raw quantity. order-service only needs to know
    // whether it can proceed; how much stock exists is inventory-service's
    // private business. Keeping the contract narrow means stock levels can be
    // remodelled later without breaking any caller.
    private boolean isInStock;
}
