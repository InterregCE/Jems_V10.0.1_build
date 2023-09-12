ALTER TABLE report_project_partner_expenditure_co_financing
    MODIFY COLUMN partner_contribution_total_eligible_after_control          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN public_contribution_total_eligible_after_control           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN automatic_public_contribution_total_eligible_after_control DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN private_contribution_total_eligible_after_control          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN sum_total_eligible_after_control                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_partner_lump_sum
    MODIFY COLUMN total_eligible_after_control DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_partner_unit_cost
    MODIFY COLUMN total_eligible_after_control DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_partner_investment
    MODIFY COLUMN total_eligible_after_control DECIMAL(17, 2) NOT NULL DEFAULT 0.00;
