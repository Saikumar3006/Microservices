package com.shop.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String orderNumber;

    // OrderLineItemsDto, NOT the OrderLineItems entity. This is the HTTP
    // contract: it must not be coupled to the database table, or a column
    // rename becomes a breaking API change - and callers could otherwise post
    // an "id" and write straight into your primary key.
    private List<OrderLineItemsDto> orderLineItemsList;
}
