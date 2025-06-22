INSERT INTO roles (id, name) VALUES
                                 (1, 'USER'),
                                 (2, 'ADMIN');

INSERT INTO app_users (id, username, password_hash, role_id) VALUES
    (nextval('app_user_sequence'), 'user', '$2a$10$WREz690LFGn9t4C83ebqOuGAO/NzQITyFQAWUQkC8kIm4heFFkt9C', 1);

INSERT INTO app_users (id, username, password_hash, role_id) VALUES
    (nextval('app_user_sequence'), 'admin', '$2a$10$s8Q9rE5tpLiTq38pvUDfkeMWpM8lFMBr64XDpMsLBgWsLw6t0K05O', 2);