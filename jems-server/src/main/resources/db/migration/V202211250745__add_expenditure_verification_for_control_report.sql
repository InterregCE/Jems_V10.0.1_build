ALTER TABLE report_project_partner_expenditure
    ADD COLUMN part_of_sample BOOLEAN DEFAULT FALSE AFTER file_id,
    ADD COLUMN certified_amount DECIMAL(17, 2) UNSIGNED DEFAULT 0.00 AFTER part_of_sample,
    ADD COLUMN deducted_amount DECIMAL(17, 2) DEFAULT 0.00 AFTER certified_amount,
    ADD COLUMN typology_of_error_id INT UNSIGNED DEFAULT NULL AFTER deducted_amount,
    ADD COLUMN verification_comment VARCHAR(1000) DEFAULT NULL AFTER typology_of_error_id,
    ADD CONSTRAINT fk_report_expenditure_to_typology_of_error
            FOREIGN KEY (typology_of_error_id) REFERENCES programme_typology_errors (id)
                ON DELETE SET NULL
                ON UPDATE RESTRICT;

UPDATE report_project_partner_expenditure SET certified_amount = declared_amount_after_submission;
