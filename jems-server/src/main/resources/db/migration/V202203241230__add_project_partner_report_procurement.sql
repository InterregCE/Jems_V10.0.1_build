CREATE TABLE report_project_partner_procurement
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id       INT UNSIGNED NOT NULL,
    contract_id     VARCHAR(30)    NOT NULL,
    contract_amount DECIMAL(15, 2) NOT NULL,
    supplier_name   VARCHAR(30)    NOT NULL,
    CONSTRAINT fk_report_partner_procurement_to_report_partner
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_procurement_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    contract_type    VARCHAR(30) DEFAULT NULL,
    comment          TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_partner_report_proc_transl_to_partner_report_proc
        FOREIGN KEY (source_entity_id) REFERENCES report_project_partner_procurement (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE report_project_partner_expenditure
    DROP COLUMN contract_id,
    DROP COLUMN investment_number;

ALTER TABLE report_project_partner_expenditure
    ADD COLUMN investment_id INT UNSIGNED DEFAULT NULL AFTER cost_category,
    ADD COLUMN procurement_id INT UNSIGNED DEFAULT NULL AFTER investment_id,
    ADD CONSTRAINT fk_partner_report_expenditure_to_procurement
        FOREIGN KEY (procurement_id) REFERENCES report_project_partner_procurement (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;
