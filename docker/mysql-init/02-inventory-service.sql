-- Bootstrap for inventory-service. Runs ONCE, on first startup of the mysql
-- container, while /var/lib/mysql is still empty.
--
-- Note this file creates the DATABASE as well as the user. MYSQL_DATABASE in
-- docker-compose.yml can only create one database, and that one is taken by
-- order-service - so every additional service creates its own here.
--
-- The TABLES inside it are not created here: Flyway does that on app startup
-- (V1 creates t_inventory, V2 seeds the stock rows). Bootstrap SQL creates the
-- empty container; migrations fill it.

CREATE DATABASE IF NOT EXISTS `inventory-service`;

-- Least privilege, same as orderuser: rights on this one database only, so a
-- bug in inventory-service cannot reach order-service's tables even though
-- both live on the same MySQL server.
CREATE USER IF NOT EXISTS 'inventoryuser'@'%' IDENTIFIED BY 'inventorypass';
GRANT ALL PRIVILEGES ON `inventory-service`.* TO 'inventoryuser'@'%';

FLUSH PRIVILEGES;
