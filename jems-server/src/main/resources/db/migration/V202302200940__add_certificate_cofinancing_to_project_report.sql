CREATE TABLE report_project_certificate_co_financing
(
    report_id                                         INT UNSIGNED NOT NULL PRIMARY KEY,

    partner_contribution_total                        DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    public_contribution_total                         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    automatic_public_contribution_total               DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    private_contribution_total                        DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    sum_total                                         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    partner_contribution_current                      DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    public_contribution_current                       DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    automatic_public_contribution_current             DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    private_contribution_current                      DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    sum_current                                       DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    partner_contribution_previously_reported          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    public_contribution_previously_reported           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    automatic_public_contribution_previously_reported DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    private_contribution_previously_reported          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    sum_previously_reported                           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_report_certificate_co_financing_to_report_project
        FOREIGN KEY (report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_co_financing
(
    report_id           INT UNSIGNED NOT NULL,
    fund_sort_number    TINYINT UNSIGNED NOT NULL,
    programme_fund_id   INT UNSIGNED DEFAULT NULL,
    percentage          DECIMAL(11, 2) NOT NULL,
    total               DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    `current`           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    previously_reported DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    previously_paid     DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    PRIMARY KEY (report_id, fund_sort_number),
    CONSTRAINT fk_report_project_co_financing_to_report_project
        FOREIGN KEY (report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_project_to_programme_fund
        FOREIGN KEY (programme_fund_id)
            REFERENCES programme_fund (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

