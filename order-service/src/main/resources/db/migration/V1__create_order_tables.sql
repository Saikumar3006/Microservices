-- Flyway naming: V<version>__<description>.sql  (note the DOUBLE underscore).
-- Flyway runs these in version order on startup and records each one in a
-- `flyway_schema_history` table, so a migration is never applied twice.
-- Once committed, a migration is immutable - changes go in a new V2 file.

CREATE TABLE t_orders (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    order_number VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE t_order_line_items (
    id       BIGINT NOT NULL AUTO_INCREMENT,
    sku_code VARCHAR(255),
    price    DECIMAL(38, 2),
    quantity INT,
    -- Foreign key created by @JoinColumn(name = "order_id") on Order.
    -- Column names are snake_case because Spring Boot's default naming
    -- strategy converts camelCase fields (skuCode -> sku_code).
    order_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_line_items_order
        FOREIGN KEY (order_id) REFERENCES t_orders (id)
);
