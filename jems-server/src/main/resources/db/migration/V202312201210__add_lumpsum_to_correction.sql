ALTER TABLE audit_control_correction

    ADD COLUMN project_lump_sum_id INT UNSIGNED DEFAULT NULL AFTER partner_report_id,
    ADD COLUMN project_lump_sum_order_nr TINYINT UNSIGNED DEFAULT NULL AFTER project_lump_sum_id,
    ADD CONSTRAINT fk_correction_lump_sum_to_project_lump_sum
        FOREIGN KEY (project_lump_sum_id, project_lump_sum_order_nr) REFERENCES project_lump_sum (project_id, order_nr)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    ADD COLUMN lump_sum_partner_id INT UNSIGNED DEFAULT NULL AFTER project_lump_sum_order_nr,
        ADD CONSTRAINT fk_correction_to_project_partner
            FOREIGN KEY (lump_sum_partner_id) REFERENCES project_partner (id)
                ON DELETE CASCADE
                ON UPDATE RESTRICT;
