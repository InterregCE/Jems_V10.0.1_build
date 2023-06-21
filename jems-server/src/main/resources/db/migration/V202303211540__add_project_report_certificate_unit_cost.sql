CREATE TABLE report_project_certificate_unit_cost
(
    id                      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id               INT UNSIGNED NOT NULL,
    programme_unit_cost_id  INT UNSIGNED NOT NULL,

    total                   DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    current                 DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    previously_reported     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_report_certificate_unit_cost_to_report_project
    FOREIGN KEY (report_id) REFERENCES report_project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,

    CONSTRAINT fk_report_certificate_unit_cost_to_programme_unit_cost
        FOREIGN KEY (programme_unit_cost_id) REFERENCES programme_unit_cost (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);
