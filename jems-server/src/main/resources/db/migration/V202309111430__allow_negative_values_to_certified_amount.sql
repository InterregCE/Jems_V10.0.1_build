ALTER TABLE report_project_partner_expenditure
    MODIFY COLUMN declared_amount DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN certified_amount DECIMAL(17, 2) NOT NULL DEFAULT 0.00;
