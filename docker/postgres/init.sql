CREATE SCHEMA IF NOT EXISTS customer_schema;
CREATE SCHEMA IF NOT EXISTS order_schema;
CREATE SCHEMA IF NOT EXISTS notification_schema;

CREATE USER customer_app WITH PASSWORD 'customer_password';
CREATE USER order_app WITH PASSWORD 'order_password';
CREATE USER notif_app WITH PASSWORD 'notif_password';

GRANT USAGE, CREATE ON SCHEMA customer_schema TO customer_app;
GRANT USAGE, CREATE ON SCHEMA order_schema TO order_app;
GRANT USAGE, CREATE ON SCHEMA notification_schema TO notif_app;

ALTER DEFAULT PRIVILEGES IN SCHEMA customer_schema GRANT ALL ON TABLES TO customer_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA order_schema GRANT ALL ON TABLES TO order_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA notification_schema GRANT ALL ON TABLES TO notif_app;