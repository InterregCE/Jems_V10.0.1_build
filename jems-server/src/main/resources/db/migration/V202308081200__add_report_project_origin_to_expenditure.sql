ALTER TABLE report_project_partner_expenditure
    ADD COLUMN report_project_of_origin_id INT UNSIGNED DEFAULT NULL AFTER report_of_origin_id,

    ADD CONSTRAINT fk_report_partner_expenditure_to_report_project_origin
        FOREIGN KEY (report_project_of_origin_id) REFERENCES report_project (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;
