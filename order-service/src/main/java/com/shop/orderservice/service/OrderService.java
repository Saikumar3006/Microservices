package com.shop.orderservice.service;

import com.shop.orderservice.dto.OrderLineItemsDto;
import com.shop.orderservice.dto.OrderRequest;
import com.shop.orderservice.model.Order;
import com.shop.orderservice.model.OrderLineItems;
import com.shop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsList().stream()
                .map(dto -> mapToEntity(dto, order))
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        orderRepository.save(order);
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
