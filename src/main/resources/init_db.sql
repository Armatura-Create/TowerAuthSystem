CREATE TABLE IF NOT EXISTS players
(
    uuid     CHAR(36)    NOT NULL PRIMARY KEY,
    name     VARCHAR(70) NOT NULL UNIQUE,
    email    VARCHAR(70) NOT NULL UNIQUE,
    password VARCHAR(70) NOT NULL
);

CREATE TABLE IF NOT EXISTS auth_players
(
    player_uuid   CHAR(36)    NOT NULL PRIMARY KEY,
    login_ip      VARCHAR(15) NOT NULL,
    reg_ip        VARCHAR(15) NOT NULL,
    last_login    TIMESTAMP   NOT NULL,
    time_reg      TIMESTAMP   NOT NULL,
    captcha_valid BOOLEAN     NOT NULL
);