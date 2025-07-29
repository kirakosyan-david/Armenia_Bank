CREATE USER "audit-service" WITH PASSWORD 'audit-service';
CREATE DATABASE "audit-service";
GRANT ALL PRIVILEGES ON DATABASE "audit-service" TO "audit-service";

CREATE USER "auth-service" WITH PASSWORD 'auth-service';
CREATE DATABASE "auth-service";
GRANT ALL PRIVILEGES ON DATABASE "auth-service" TO "auth-service";
