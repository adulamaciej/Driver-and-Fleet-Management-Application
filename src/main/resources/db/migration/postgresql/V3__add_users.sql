CREATE SEQUENCE app_user_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE app_users (
                           id BIGINT PRIMARY KEY,
                           username VARCHAR(50) NOT NULL UNIQUE,
                           password_hash VARCHAR(255) NOT NULL,
                           role VARCHAR(20) NOT NULL
);

CREATE INDEX idx_app_users_username ON app_users(username);