CREATE TABLE report_project_partner_expenditure_investment
(
    id                                                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id                                         INT UNSIGNED NOT NULL,
    investment_id                                     INT UNSIGNED NOT NULL,
    investment_number                                 INT UNSIGNED NOT NULL,
    work_package_number                               INT UNSIGNED NOT NULL,

    total                                             DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    `current`                                         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    previously_reported                               DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_report_partner_exp_investment_to_report_partner
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
