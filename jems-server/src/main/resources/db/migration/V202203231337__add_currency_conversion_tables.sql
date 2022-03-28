CREATE TABLE currency_rate
(
    code            VARCHAR(3)              NOT NULL,
    year            SMALLINT       UNSIGNED NOT NULL,
    month           TINYINT        UNSIGNED NOT NULL,
    name            VARCHAR(128)            NOT NULL,
    conversion_rate DECIMAL(15, 6) UNSIGNED NOT NULL,
    PRIMARY KEY (code, year, month)
);
