CREATE SEQUENCE app_user_sequence START WITH 1 INCREMENT BY 1;


CREATE TABLE roles (
                       id BIGINT PRIMARY KEY,
                       name VARCHAR(20) NOT NULL UNIQUE);


CREATE TABLE app_users (
                           id BIGINT PRIMARY KEY,
                           username VARCHAR(50) NOT NULL UNIQUE,
                           password_hash VARCHAR(255) NOT NULL,
                           role_id BIGINT NOT NULL REFERENCES roles(id));


CREATE INDEX idx_app_users_username ON app_users(username);