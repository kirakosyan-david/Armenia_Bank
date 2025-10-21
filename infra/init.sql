CREATE USER auth_service WITH PASSWORD 'auth_service';
CREATE USER audit_service WITH PASSWORD 'audit_service';
CREATE USER wallet_service WITH PASSWORD 'wallet_service';
CREATE USER transaction_service WITH PASSWORD 'transaction_service';
CREATE USER notification_service WITH PASSWORD 'notification_service';

CREATE DATABASE auth_service OWNER auth_service;
CREATE DATABASE audit_service OWNER audit_service;
CREATE DATABASE wallet_service OWNER wallet_service;
CREATE DATABASE transaction_service OWNER transaction_service;
CREATE DATABASE notification_service OWNER notification_service;

\c auth_service
CREATE SCHEMA IF NOT EXISTS auth_service;
GRANT ALL ON SCHEMA auth_service TO auth_service;
ALTER DATABASE auth_service SET search_path TO auth_service, public;

\c audit_service
CREATE SCHEMA IF NOT EXISTS audit_service;
GRANT ALL ON SCHEMA audit_service TO audit_service;
ALTER DATABASE audit_service SET search_path TO audit_service, public;

\c wallet_service
CREATE SCHEMA IF NOT EXISTS wallet_service;
GRANT ALL ON SCHEMA wallet_service TO wallet_service;
ALTER DATABASE wallet_service SET search_path TO wallet_service, public;

\c transaction_service
CREATE SCHEMA IF NOT EXISTS transaction_service;
GRANT ALL ON SCHEMA transaction_service TO transaction_service;
ALTER DATABASE transaction_service SET search_path TO transaction_service, public;

\c notification_service
CREATE SCHEMA IF NOT EXISTS notification_service;
GRANT ALL ON SCHEMA notification_service TO notification_service;
ALTER DATABASE notification_service SET search_path TO notification_service, public;