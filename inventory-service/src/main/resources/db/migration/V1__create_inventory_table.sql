-- Schema only. Seed data lives in V2, kept separate so the two can evolve
-- independently (and so a future environment could skip the seed if needed).

CREATE TABLE t_inventory (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    sku_code VARCHAR(255) NOT NULL,
    quantity INT          NOT NULL,
    PRIMARY KEY (id),
    -- One row per SKU. A duplicate would make "is this in stock?" ambiguous,
    -- so let the database enforce it rather than trusting application code.
    CONSTRAINT uk_inventory_sku_code UNIQUE (sku_code)
);
