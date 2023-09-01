ALTER TABLE report_project
    ADD COLUMN risk_based_verification BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN risk_based_verification_description TEXT(5000) DEFAULT NULL;

CREATE TABLE report_project_verification_expenditure
(
    expenditure_id               INT UNSIGNED NOT NULL PRIMARY KEY,
    part_of_verification_sample  BOOLEAN NOT NULL DEFAULT FALSE,
    deducted_by_js               DECIMAL(17, 2) NOT NULL,
    deducted_by_ma               DECIMAL(17, 2) NOT NULL,
    amount_after_verification    DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    typology_of_error_id         INT UNSIGNED   DEFAULT NULL,
    parked                       BOOLEAN NOT NULL DEFAULT FALSE,
    verification_comment TEXT(1000) DEFAULT NULL,

    CONSTRAINT fk_project_verification_to_project_partner_expenditure
        FOREIGN KEY (expenditure_id) REFERENCES report_project_partner_expenditure (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE report_project_partner_expenditure_parked
    ADD COLUMN report_project_of_origin_id    INT UNSIGNED DEFAULT NULL AFTER report_of_origin_id,

    ADD CONSTRAINT fk_report_partner_exp_to_report_project_origin
        FOREIGN KEY (report_project_of_origin_id) REFERENCES report_project (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;
