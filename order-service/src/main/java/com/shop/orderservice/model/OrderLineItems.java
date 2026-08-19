package com.shop.orderservice.model;



import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="t_order_line_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;

    // The OWNING side of the relationship. The order_id column physically
    // lives on THIS table, so this is the class that should declare it.
    // Because the entity now knows the column, Hibernate can include order_id
    // in the INSERT instead of inserting NULL and patching it with an UPDATE.
    //
    // @ManyToOne: many line items belong to one order.
    // fetch = LAZY: the default for @ManyToOne is EAGER, which would load the
    // parent Order every time a line item is read, even when unused.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
