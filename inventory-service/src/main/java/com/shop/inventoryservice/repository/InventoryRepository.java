package com.shop.inventoryservice.repository;

import com.shop.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Spring Data writes the query from the method NAME - no SQL needed.
    // findBySkuCodeIn(...) becomes: SELECT * FROM t_inventory WHERE sku_code IN (?)
    // One query for all the sku codes, rather than one query per sku.
    List<Inventory> findBySkuCodeIn(List<String> skuCodes);
}
