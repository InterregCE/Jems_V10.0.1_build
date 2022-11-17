CREATE TABLE project_contracting_partner_banking_details
(
    partner_id     INT UNSIGNED NOT NULL PRIMARY KEY,
    account_holder     TEXT(100) DEFAULT '',
    account_number       TEXT(50) DEFAULT '',
    account_iban      TEXT(50) DEFAULT '',
    account_swift_bic_code  TEXT(50) DEFAULT '',
    bank_name     TEXT(100) DEFAULT '',
    street_name   TEXT(50) DEFAULT '',
    street_number TEXT(10) DEFAULT '',
    postal_code TEXT(10) DEFAULT '',
    country TEXT(50) DEFAULT '',
    nuts_two_region TEXT(50) DEFAULT '',
    nuts_three_region TEXT(50) DEFAULT '',
    CONSTRAINT fk_project_contracting_partner_banking_details_to_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);