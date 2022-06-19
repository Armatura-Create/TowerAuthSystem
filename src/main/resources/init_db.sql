CREATE TABLE IF NOT EXISTS players
(
    uuid     CHAR(36)    NOT NULL PRIMARY KEY,
    name     VARCHAR(70) NOT NULL UNIQUE,
    email    VARCHAR(70) NOT NULL UNIQUE,
    password VARCHAR(70) NOT NULL
);

CREATE TABLE IF NOT EXISTS auth_data
(
    player_uuid   CHAR(36)    NOT NULL PRIMARY KEY REFERENCES players(uuid),
    login_ip      VARCHAR(15) NOT NULL,
    reg_ip        VARCHAR(15) NOT NULL,
    last_login    TIMESTAMP   NOT NULL,
    time_reg      TIMESTAMP   NOT NULL,
    recovery_code CHAR(6) NULL
);
