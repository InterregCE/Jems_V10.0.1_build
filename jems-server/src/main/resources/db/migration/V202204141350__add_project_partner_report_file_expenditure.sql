ALTER TABLE report_project_partner_expenditure
    ADD COLUMN file_id INT UNSIGNED DEFAULT NULL AFTER declared_amount_after_submission,
    ADD CONSTRAINT fk_report_expenditure_to_report_file
        FOREIGN KEY(file_id) REFERENCES report_project_file(id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;
