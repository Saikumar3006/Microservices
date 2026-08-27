package com.shop.inventoryservice.controller;

import com.shop.inventoryservice.dto.InventoryResponse;
import com.shop.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // A repeated query parameter binds straight to a List, so one call can ask
    // about a whole order at once:
    //   GET /api/inventory?skuCode=iphone_13&skuCode=airpods
    // That matters because order-service will check every line item in a single
    // request rather than making one HTTP call per item.
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}
