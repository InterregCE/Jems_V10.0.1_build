CREATE TABLE report_project_partner_lump_sum
(
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_report_id     INT UNSIGNED            NOT NULL,
    programme_lump_sum_id INT UNSIGNED            NOT NULL,
    `period`              SMALLINT UNSIGNED DEFAULT NULL,
    cost                  DECIMAL(11, 2) UNSIGNED NOT NULL,
    CONSTRAINT fk_report_partner_lump_sum_to_report_partner
        FOREIGN KEY (partner_report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_lump_sum_id_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id) REFERENCES programme_lump_sum (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

ALTER TABLE report_project_partner_expenditure
    ADD COLUMN report_lump_sum_id INT UNSIGNED DEFAULT NULL AFTER partner_report_id,
    ADD COLUMN unit_cost_id       INT UNSIGNED DEFAULT NULL AFTER report_lump_sum_id,
    ADD COLUMN number_of_units    DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00 AFTER vat,
    ADD COLUMN price_per_unit     DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER number_of_units,

    ADD CONSTRAINT fk_report_lump_sum_id_to_report_lump_sum
        FOREIGN KEY (report_lump_sum_id) REFERENCES report_project_partner_lump_sum (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_unit_cost_id_to_programme_unit_cost
        FOREIGN KEY (unit_cost_id) REFERENCES programme_unit_cost (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

