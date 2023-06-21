ALTER TABLE report_project_partner_expenditure_co_financing
    ADD COLUMN partner_contribution_current_parked                          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN public_contribution_current_parked                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN automatic_public_contribution_current_parked                 DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN private_contribution_current_parked                          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN sum_current_parked                                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    ADD COLUMN partner_contribution_previously_reported_parked              DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN public_contribution_previously_reported_parked               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN automatic_public_contribution_previously_reported_parked     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN private_contribution_previously_reported_parked              DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN sum_previously_reported_parked                               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    ADD COLUMN partner_contribution_current_re_included                     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN public_contribution_current_re_included                      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN automatic_public_contribution_current_re_included            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN private_contribution_current_re_included                     DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN sum_current_re_included                                      DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_partner_co_financing
    ADD COLUMN current_parked               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN current_re_included          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN previously_reported_parked   DECIMAL(17, 2) NOT NULL DEFAULT 0.00;
