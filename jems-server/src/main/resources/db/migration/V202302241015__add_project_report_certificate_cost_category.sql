CREATE TABLE report_project_certificate_cost_category
(
    report_id                                           INT UNSIGNED NOT NULL PRIMARY KEY,

    staff_total                                         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    office_total                                        DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    travel_total                                        DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    external_total                                      DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    equipment_total                                     DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    infrastructure_total                                DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    other_total                                         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    lump_sum_total                                      DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    unit_cost_total                                     DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    sum_total                                           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    staff_current                                       DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    office_current                                      DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    travel_current                                      DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    external_current                                    DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    equipment_current                                   DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    infrastructure_current                              DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    other_current                                       DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    lump_sum_current                                    DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    unit_cost_current                                   DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    sum_current                                         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    staff_previously_reported                           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    office_previously_reported                          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    travel_previously_reported                          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    external_previously_reported                        DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    equipment_previously_reported                       DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    infrastructure_previously_reported                  DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    other_previously_reported                           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    lump_sum_previously_reported                        DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    unit_cost_previously_reported                       DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    sum_previously_reported                             DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_report_certificate_cost_category_to_report_project
        FOREIGN KEY (report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
