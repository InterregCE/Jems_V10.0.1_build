CREATE TABLE report_project_verification_contribution_source_overview
(
    id                              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_report_id               INT UNSIGNED,
    fund_id                         INT UNSIGNED DEFAULT NULL,
    fund_value                      DECIMAL(17, 2) DEFAULT NULL,
    partner_contribution            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    public_contribution             DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    automatic_public_contribution   DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    private_contribution            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    UNIQUE (partner_report_id, fund_id),

    CONSTRAINT fk_rpv_contribution_source_overview_to_report_partner
        FOREIGN KEY (partner_report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,

    CONSTRAINT fk_rpv_contribution_source_overview_to_programme_fund FOREIGN KEY (fund_id) REFERENCES programme_fund (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);