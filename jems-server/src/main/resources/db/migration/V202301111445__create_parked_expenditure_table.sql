CREATE TABLE report_project_partner_expenditure_parked
(
    parked_from_expenditure_id INT UNSIGNED PRIMARY KEY,
    report_of_origin_id        INT UNSIGNED NOT NULL,
    original_number            INT UNSIGNED NOT NULL,

    CONSTRAINT fk_report_partner_exp_parked_to_report_partner_exp
        FOREIGN KEY (parked_from_expenditure_id) REFERENCES report_project_partner_expenditure (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_partner_exp_parked_origin_to_report_partner
        FOREIGN KEY (report_of_origin_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE report_project_partner_expenditure
    ADD COLUMN un_parked_from_expenditure_id INT UNSIGNED DEFAULT NULL,
    ADD COLUMN report_of_origin_id           INT UNSIGNED DEFAULT NULL,
    ADD COLUMN original_number               INT UNSIGNED DEFAULT NULL,

    ADD CONSTRAINT fk_report_partner_exp_to_report_partner_exp_origin
        FOREIGN KEY (un_parked_from_expenditure_id) REFERENCES report_project_partner_expenditure (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,

    ADD CONSTRAINT fk_report_partner_exp_to_report_partner_origin
        FOREIGN KEY (report_of_origin_id) REFERENCES report_project_partner (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;
