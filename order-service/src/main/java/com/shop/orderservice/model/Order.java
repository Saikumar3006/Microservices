package com.shop.orderservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "t_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    // IDENTITY, not AUTO. MySQL has no sequences, so AUTO makes Hibernate fall
    // back to a separate generator table - an extra table nobody asked for.
    // IDENTITY maps to MySQL's own AUTO_INCREMENT, and matches OrderLineItems.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    // One order has MANY line items - @OneToOne on a List is a mapping error
    // and Hibernate rejects it at startup.
    //
    // @JoinColumn puts an order_id foreign key on t_order_line_items. Without
    // it, JPA would create a third "join table" to link the two, which is extra
    // machinery you do not need for a collection the order fully owns.
    //
    // cascade = ALL: saving an Order saves its line item
    // s too, so we never save
    // them separately. orphanRemoval: a line item dropped from this list is
    // deleted rather than left behind pointing at nothing.
    // mappedBy = "order" means: this side does NOT own the relationship - the
    // `order` FIELD on OrderLineItems does, and it writes the order_id column.
    // ("order" is a Java field name here, not a column name.)
    //
    // This is why OrderService must call item.setOrder(order): Hibernate only
    // ever writes the foreign key from the owning side. Setting just this list
    // and forgetting that call saves order_id as NULL, silently.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItems> orderLineItemsList;
}
