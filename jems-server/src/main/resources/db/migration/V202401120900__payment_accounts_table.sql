INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES  (1, 'PaymentsAccountRetrieve'), (1, 'PaymentsAccountUpdate');

CREATE TABLE payment_account
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    programme_fund_id      INT UNSIGNED NOT NULL,
    accounting_year_id     INT UNSIGNED NOT NULL,
    status                 ENUM('DRAFT', 'SUBMITTED') NOT NULL DEFAULT 'DRAFT',
    national_reference     VARCHAR(50) NOT NULL             DEFAULT '',
    technical_assistance   DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    submission_to_sfc_date DATETIME(3) DEFAULT NULL,
    sfc_number             VARCHAR(50) NOT NULL DEFAULT '',
    comment                TEXT(5000) NOT NULL DEFAULT '',

    CONSTRAINT constraint_unique_fund_accounting_year UNIQUE(programme_fund_id, accounting_year_id),
    CONSTRAINT fk_payment_account_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    CONSTRAINT fk_payment_account_to_accounting_years FOREIGN KEY (accounting_year_id) REFERENCES accounting_years (id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);

INSERT IGNORE INTO payment_account (programme_fund_id, accounting_year_id)
SELECT pf.id, ac.id
FROM accounting_years ac,
     programme_fund pf
WHERE pf.selected = true
GROUP BY pf.id, ac.id;

