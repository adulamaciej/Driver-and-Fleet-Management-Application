INSERT INTO app_users (id, username, password_hash, role) VALUES
    (nextval('app_user_sequence'), 'user', '$2a$10$WREz690LFGn9t4C83ebqOuGAO/NzQITyFQAWUQkC8kIm4heFFkt9C', 'USER');

INSERT INTO app_users (id, username, password_hash, role) VALUES
    (nextval('app_user_sequence'), 'admin', '$2a$10$s8Q9rE5tpLiTq38pvUDfkeMWpM8lFMBr64XDpMsLBgWsLw6t0K05O', 'ADMIN');