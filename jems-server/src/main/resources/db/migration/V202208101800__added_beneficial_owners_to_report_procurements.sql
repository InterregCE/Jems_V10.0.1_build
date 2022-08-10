CREATE TABLE report_project_partner_procurement_beneficial
(
    id                   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    procurement_id       INT UNSIGNED NOT NULL,
    created_in_report_id INT UNSIGNED NOT NULL,
    first_name           VARCHAR(255) NOT NULL,
    last_name            VARCHAR(255) NOT NULL,
    birth                DATE DEFAULT NULL,
    vat_number           VARCHAR(30)  NOT NULL,
    CONSTRAINT fk_procurement_beneficial_to_procurement
        FOREIGN KEY (procurement_id) REFERENCES report_project_partner_procurement (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_procurement_beneficial_to_report_partner
        FOREIGN KEY (created_in_report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
