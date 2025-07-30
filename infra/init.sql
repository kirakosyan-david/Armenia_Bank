-- Создание пользователей
CREATE USER auth_service WITH PASSWORD 'auth_service';
CREATE USER audit_service WITH PASSWORD 'audit_service';

-- Создание баз данных
CREATE DATABASE auth_service OWNER auth_service;
CREATE DATABASE audit_service OWNER audit_service;

-- Подключение к базе auth_service и создание схемы
\c auth_service
CREATE SCHEMA IF NOT EXISTS auth_service;
GRANT ALL ON SCHEMA auth_service TO auth_service;
ALTER DATABASE auth_service SET search_path TO auth_service, public;

-- Подключение к базе audit_service и создание схемы
\c audit_service
CREATE SCHEMA IF NOT EXISTS audit_service;
GRANT ALL ON SCHEMA audit_service TO audit_service;
ALTER DATABASE audit_service SET search_path TO audit_service, public;