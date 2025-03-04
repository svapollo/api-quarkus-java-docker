CREATE DATABASE quarkus-social;

CREATE TABLE users(
    id bigserial NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age integer NOT NULL
);

CREATE TABLE posts(
    id bigserial NOT NULL PRIMARY KEY,
    post_text VARCHAR(150) NOT NULL,
    post_datetime TIMESTAMP NOT NULL,
    user_id bigint NOT NULL REFERENCES users(id)
);

CREATE TABLE followers(
    id bigserial NOT NULL PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES users(id),
    follower_id bigint NOT NULL REFERENCES users(id)
);