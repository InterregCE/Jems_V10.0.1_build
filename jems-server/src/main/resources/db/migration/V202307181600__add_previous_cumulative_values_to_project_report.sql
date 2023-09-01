ALTER TABLE report_project_certificate_co_financing
    ADD COLUMN partner_contribution_current_verified             DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN public_contribution_current_verified              DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN automatic_public_contribution_current_verified    DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN private_contribution_current_verified             DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN sum_current_verified                              DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN partner_contribution_previously_verified          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN public_contribution_previously_verified           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN automatic_public_contribution_previously_verified DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN private_contribution_previously_verified          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN sum_previously_verified                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_co_financing
    DROP COLUMN percentage,
    ADD COLUMN current_verified    DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported,
    ADD COLUMN previously_verified DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_verified;
