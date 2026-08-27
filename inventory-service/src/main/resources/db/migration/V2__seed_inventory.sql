-- Starting stock, versioned like any other migration so every clone of this
-- repo comes up with the same data and nobody has to INSERT by hand.
--
-- airpods is deliberately at 0: order-service needs a SKU that exists but is
-- OUT of stock, to exercise the rejection path when it starts calling here.

INSERT INTO t_inventory (sku_code, quantity) VALUES
    ('iphone_13',   100),
    ('macbook_pro',  50),
    ('ipad_air',     25),
    ('airpods',       0);
