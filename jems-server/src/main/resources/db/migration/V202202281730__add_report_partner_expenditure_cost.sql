CREATE TABLE partner_report_expenditure_cost
(
    id                        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_report_id         INT UNSIGNED NOT NULL,
    cost_category             VARCHAR(127) NOT NULL,
    investment_number         VARCHAR(127) DEFAULT NULL,
    contract_id               VARCHAR(127) DEFAULT NULL,
    invoice_number            VARCHAR(127) DEFAULT NULL,
    internal_reference_number VARCHAR(127) DEFAULT NULL,
    invoice_date              DATETIME(3) DEFAULT NULL,
    date_of_payment           DATETIME(3) DEFAULT NULL,
    total_value_invoice       DECIMAL(17, 2) UNSIGNED DEFAULT 0.00,
    vat                       DECIMAL(17, 2) UNSIGNED DEFAULT 0.00,
    declared_amount           DECIMAL(17, 2) UNSIGNED DEFAULT 0.00,

    CONSTRAINT fk_partner_report_to_partner_report_expenditure_cost
        FOREIGN KEY (partner_report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE partner_report_expenditure_cost_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    comment          TEXT(255) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_partner_report_expenditure_cost_transl_to_partner_rep_exp
        FOREIGN KEY (source_entity_id) REFERENCES partner_report_expenditure_cost (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
