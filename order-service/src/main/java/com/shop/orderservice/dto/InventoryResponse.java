package com.shop.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * order-service's OWN copy of inventory-service's response shape.
 * <p>
 * Deliberately duplicated rather than shared through a common library: a shared
 * DTO jar couples the two services' release cycles, so changing a field would
 * mean redeploying both together. This class is order-service's understanding
 * of the contract, and it only needs the fields order-service actually reads.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private String skuCode;

    // NOTE the field name. inventory-service declares `boolean isInStock`, but
    // Jackson derives the JSON property from the GETTER (isInStock()) and strips
    // the "is" prefix - so the wire format is {"inStock": true}.
    // Naming the field `inStock` here makes it line up exactly. Calling it
    // `isInStock` would look more symmetrical and silently deserialise to false.
    private boolean inStock;
}
