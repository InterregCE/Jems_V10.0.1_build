ALTER TABLE report_project_partner
    ADD COLUMN project_report_id INT UNSIGNED DEFAULT NULL,
    ADD CONSTRAINT fk_report_partner_to_report_project
    FOREIGN KEY (project_report_id) REFERENCES report_project (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT;
