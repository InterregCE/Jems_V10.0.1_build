ALTER TABLE report_project_partner_expenditure_co_financing
    ADD COLUMN partner_contribution_previously_validated          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN public_contribution_previously_validated           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN automatic_public_contribution_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN private_contribution_previously_validated          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN sum_previously_validated                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_partner_co_financing
    ADD COLUMN previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported;
