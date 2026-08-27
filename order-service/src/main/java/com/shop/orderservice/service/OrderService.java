package com.shop.orderservice.service;

import com.shop.orderservice.client.InventoryClient;
import com.shop.orderservice.dto.InventoryResponse;
import com.shop.orderservice.dto.OrderLineItemsDto;
import com.shop.orderservice.dto.OrderRequest;
import com.shop.orderservice.exception.OutOfStockException;
import com.shop.orderservice.exception.UnknownSkuException;
import com.shop.orderservice.model.Order;
import com.shop.orderservice.model.OrderLineItems;
import com.shop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    /**
     * Deliberately NOT annotated @Transactional.
     * <p>
     * A transaction holds a database connection for its whole duration. If the
     * HTTP call to inventory-service sat inside one, every in-flight order would
     * pin a connection while waiting on the network. Under load the connection
     * pool empties and the service stalls - on a dependency that is not even the
     * database.
     * <p>
     * So: make the remote call first, with no transaction open, and only then
     * persist. The save() below opens its own short transaction internally.
     */
    public String placeOrder(OrderRequest orderRequest) {
        List<OrderLineItemsDto> requestedItems = orderRequest.getOrderLineItemsList();
        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one line item");
        }

        List<String> skuCodes = requestedItems.stream()
                .map(OrderLineItemsDto::getSkuCode)
                .toList();

        // --- remote call: no transaction is open here ---
        List<InventoryResponse> inventory = inventoryClient.isInStock(skuCodes);

        // Unknown SKUs are ABSENT from the response, not returned as false, so
        // they must be detected by comparing what came back against what we
        // asked for. Skipping this check would let an order for a nonexistent
        // product succeed, because "every returned item is in stock" is
        // vacuously true when nothing was returned.
        Set<String> known = inventory.stream()
                .map(InventoryResponse::getSkuCode)
                .collect(Collectors.toSet());

        List<String> unknown = skuCodes.stream()
                .filter(sku -> !known.contains(sku))
                .distinct()
                .toList();
        if (!unknown.isEmpty()) {
            throw new UnknownSkuException(unknown);
        }

        List<String> outOfStock = inventory.stream()
                .filter(response -> !response.isInStock())
                .map(InventoryResponse::getSkuCode)
                .toList();
        if (!outOfStock.isEmpty()) {
            throw new OutOfStockException(outOfStock);
        }

        // --- everything checks out, now persist ---
        String orderNumber = persist(orderRequest);
        log.info("Order {} placed for {}", orderNumber, skuCodes);
        return orderNumber;
    }

    // NOT annotated @Transactional, on purpose. Spring applies @Transactional
    // through a proxy, and placeOrder() calls this method directly on `this` -
    // self-invocation never goes through the proxy, so the annotation would be
    // silently ignored. It would look correct and do nothing.
    //
    // No annotation is needed anyway: orderRepository.save() is itself
    // transactional, and the cascade writes the order and its line items in
    // that one transaction - all or nothing.
    private String persist(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsList().stream()
                .map(dto -> mapToEntity(dto, order))
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        orderRepository.save(order);
        return order.getOrderNumber();
    }

    // Renamed from mapToDTO: it maps a DTO -> an entity, which is the opposite
    // direction. The old version assigned backwards - it read the empty new
    // entity and wrote those nulls into the incoming object, then returned the
    // still-empty entity. Every line item would have saved as (null, null, null).
    private OrderLineItems mapToEntity(OrderLineItemsDto dto, Order order) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(dto.getSkuCode());
        orderLineItems.setPrice(dto.getPrice());
        orderLineItems.setQuantity(dto.getQuantity());
        // Required now that OrderLineItems owns the relationship. Without this
        // line order_id saves as NULL - no error, just a broken row.
        orderLineItems.setOrder(order);
        return orderLineItems;
    }
}
