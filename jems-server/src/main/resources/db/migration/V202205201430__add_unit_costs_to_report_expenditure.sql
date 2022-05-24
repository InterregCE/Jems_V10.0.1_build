CREATE TABLE report_project_partner_unit_cost
(
    id                                      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_report_id                       INT UNSIGNED            NOT NULL,
    programme_unit_cost_id                  INT UNSIGNED            NOT NULL,
    total_cost                              DECIMAL(17, 2) UNSIGNED NOT NULL,
    number_of_units                         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    CONSTRAINT fk_report_partner_unit_cost_to_report_partner
        FOREIGN KEY (partner_report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_lump_sum_id_to_programme_unit_cost
        FOREIGN KEY (programme_unit_cost_id) REFERENCES programme_unit_cost (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

ALTER TABLE report_project_partner_expenditure
    DROP FOREIGN KEY fk_unit_cost_id_to_programme_unit_cost,
    DROP COLUMN unit_cost_id,
    ADD COLUMN report_unit_cost_id       INT UNSIGNED DEFAULT NULL AFTER report_lump_sum_id,

    ADD CONSTRAINT fk_report_unit_cost_id_to_report_unit_cost
        FOREIGN KEY (report_unit_cost_id) REFERENCES report_project_partner_unit_cost (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;
