package com.shop.inventoryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    // IDENTITY maps to MySQL AUTO_INCREMENT. AUTO would make Hibernate emulate
    // a sequence with an extra generator table, since MySQL has no sequences.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The SKU is how OTHER services refer to a product - order-service will
    // send sku codes, not database ids. Ids are private to each service.
    private String skuCode;

    private Integer quantity;
}
