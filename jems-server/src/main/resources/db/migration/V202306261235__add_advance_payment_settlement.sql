CREATE TABLE payment_advance_settlement
(
    id                       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    number                   INT NOT NULL,
    advance_payment_id       INT UNSIGNED NOT NULL,
    amount_settled           DECIMAL(11, 2) NOT NULL,
    settlement_date          DATETIME(3) NOT NULL,
    comment                  TEXT(500),

    CONSTRAINT fk_payment_settlement_to_payment_advance FOREIGN KEY (advance_payment_id) REFERENCES payment_advance (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);