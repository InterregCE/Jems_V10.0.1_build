CREATE TABLE payment_partner_installment
(
    id                       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_partner_id       INT UNSIGNED NOT NULL,
    amount_paid              DECIMAL(11, 2),
    payment_date             DATETIME(3) DEFAULT NULL,
    comment                  TEXT(500),
    is_save_payment_info          BOOLEAN,
    save_payment_info_account_id  INT UNSIGNED,
    save_payment_date             DATETIME(3)  DEFAULT NULL,
    is_payment_confirmed          BOOLEAN,
    payment_confirmed_account_id  INT UNSIGNED,
    payment_confirmed_date        DATETIME(3)  DEFAULT NULL,

    CONSTRAINT fk_payment_inst_save_payment_to_account FOREIGN KEY (save_payment_info_account_id) REFERENCES account (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_inst_conf_payment_to_account FOREIGN KEY (payment_confirmed_account_id) REFERENCES account (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_inst_to_payment_partner FOREIGN KEY (payment_partner_id) REFERENCES payment_partner (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);
