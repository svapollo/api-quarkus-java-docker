CREATE DATABASE IF NOT EXISTS quarkussocial;

USE quarkussocial;

CREATE TABLE users(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL
);

CREATE TABLE posts(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    post_text VARCHAR(150) NOT NULL,
    post_datetime TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE followers(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    follower_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (follower_id) REFERENCES users(id)
);