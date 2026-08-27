-- Runs ONCE, on the very first startup of the mysql container, while
-- /var/lib/mysql is still empty. Mounted into /docker-entrypoint-initdb.d.
-- Editing this file later has no effect unless the volume is destroyed
-- (docker compose down -v).
--
-- The `order-service` database itself is created by MYSQL_DATABASE in
-- docker-compose.yml, so it already exists by the time this runs.
-- This script only creates the account the application uses.

-- '%' means "connecting from any host". Inside Docker the app arrives via the
-- container network (or the published port), never as 'localhost', so
-- 'orderuser'@'localhost' would refuse every real connection.
CREATE USER IF NOT EXISTS 'orderuser'@'%' IDENTIFIED BY 'orderpass';

-- Least privilege: full rights on this ONE database, nothing anywhere else.
-- The app can create tables (Flyway needs that), read and write its own data,
-- and cannot touch any other schema on this server.
-- Backticks are required because the name contains a hyphen.
GRANT ALL PRIVILEGES ON `order-service`.* TO 'orderuser'@'%';

FLUSH PRIVILEGES;
