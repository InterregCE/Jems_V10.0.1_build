ALTER TABLE report_project_partner_procurement
    ADD COLUMN file_id INT UNSIGNED DEFAULT NULL AFTER supplier_name,
    ADD CONSTRAINT fk_report_procurement_to_report_file
        FOREIGN KEY(file_id) REFERENCES report_project_file(id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;
