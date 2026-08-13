package com.shop.orderservice.service;

import com.shop.orderservice.dto.OrderLineItemsDto;
import com.shop.orderservice.dto.OrderRequest;
import com.shop.orderservice.model.Order;
import com.shop.orderservice.model.OrderLineItems;
import com.shop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList=orderRequest.getOrderLineItemsList().stream()
                .map(this::mapToDTO)
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        orderRepository.save(order);
    }

    private OrderLineItems mapToDTO(OrderLineItems orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItemsDto.setPrice(orderLineItems.getPrice());
        orderLineItemsDto.setQuantity(orderLineItems.getQuantity());
        orderLineItemsDto.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }
}
