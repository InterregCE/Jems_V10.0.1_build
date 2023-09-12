ALTER TABLE report_project_spending_profile
    MODIFY COLUMN previously_reported DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN currently_reported DECIMAL(17, 2) NOT NULL DEFAULT 0.00;
