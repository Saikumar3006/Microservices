package com.shop.orderservice.controller;

import com.shop.orderservice.dto.OrderRequest;
import com.shop.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Returns the generated order number instead of a fixed string - the caller
    // needs something to refer to the order by afterwards.
    // Failures are not handled here: GlobalExceptionHandler turns them into the
    // right status codes, which keeps this method to its happy path.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> placeOrder(@RequestBody OrderRequest orderRequest) {
        String orderNumber = orderService.placeOrder(orderRequest);
        return Map.of(
                "orderNumber", orderNumber,
                "message", "Order placed successfully");
    }
}
