ALTER TABLE report_project_partner_contribution
    ADD COLUMN file_id INT UNSIGNED DEFAULT NULL AFTER currently_reported,
    ADD CONSTRAINT fk_report_contribution_to_report_file
        FOREIGN KEY(file_id) REFERENCES report_project_file(id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;
