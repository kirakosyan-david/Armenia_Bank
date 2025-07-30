-- Создание пользователей (если не существуют)
CREATE USER "auth-service" WITH PASSWORD 'auth-service';
CREATE USER "audit-service" WITH PASSWORD 'audit-service';

-- Создание баз данных
CREATE DATABASE "auth-service" OWNER "auth-service";
CREATE DATABASE "audit-service" OWNER "audit-service";
