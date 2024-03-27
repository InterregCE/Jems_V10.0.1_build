CREATE TABLE payment_account_reconciliation
(
    id                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    payment_account_id INT UNSIGNED NOT NULL,
    priority_axis_id   INT UNSIGNED NOT NULL,
    total_comment      VARCHAR(500) NOT NULL DEFAULT "",
    aa_comment         VARCHAR(500) NOT NULL DEFAULT "",
    ec_comment         VARCHAR(500) NOT NULL DEFAULT "",

    CONSTRAINT unique_payment_account_and_priority UNIQUE (payment_account_id, priority_axis_id),
    CONSTRAINT fk_payment_account_reconciliation_to_payment_account
        FOREIGN KEY (payment_account_id) REFERENCES payment_account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_account_reconciliation_to_programme_priority
        FOREIGN KEY (priority_axis_id) REFERENCES programme_priority (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
)
