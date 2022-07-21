ALTER TABLE report_project_partner_co_financing
    ADD COLUMN total DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER percentage,
    ADD COLUMN `current` DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER total,
    ADD COLUMN previously_reported DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER `current`;

CREATE TABLE report_project_partner_expenditure_co_financing
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

    CONSTRAINT fk_report_partner_exp_co_financing_to_report_partner
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
