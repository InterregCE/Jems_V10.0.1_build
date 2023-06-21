CREATE TABLE report_project_certificate_lump_sum
(
    id                      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id               INT UNSIGNED NOT NULL,
    programme_lump_sum_id   INT UNSIGNED NOT NULL,

    period_number                SMALLINT UNSIGNED DEFAULT NULL,
    order_nr                TINYINT UNSIGNED NOT NULL,
    total                   DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    current                 DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    previously_reported     DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    previously_paid         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_report_certificate_lump_sum_to_report_project
    FOREIGN KEY (report_id) REFERENCES report_project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,

    CONSTRAINT fk_report_certificate_lump_sum_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id) REFERENCES programme_lump_sum (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);
