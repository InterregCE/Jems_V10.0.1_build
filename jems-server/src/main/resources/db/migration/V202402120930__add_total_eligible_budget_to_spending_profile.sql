ALTER TABLE report_project_spending_profile
    ADD COLUMN partner_total_eligible_budget DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00;
