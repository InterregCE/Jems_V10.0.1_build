ALTER TABLE report_project_certificate_co_financing
    MODIFY COLUMN partner_contribution_total                        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN public_contribution_total                         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN automatic_public_contribution_total               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN private_contribution_total                        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN sum_total                                         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    MODIFY COLUMN partner_contribution_current                      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN public_contribution_current                       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN automatic_public_contribution_current             DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN private_contribution_current                      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN sum_current                                       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    MODIFY COLUMN partner_contribution_previously_reported          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN public_contribution_previously_reported           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN automatic_public_contribution_previously_reported DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN private_contribution_previously_reported          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN sum_previously_reported                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00;
