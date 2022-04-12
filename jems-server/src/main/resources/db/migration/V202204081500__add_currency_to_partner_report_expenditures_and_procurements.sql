UPDATE report_project_partner_expenditure
SET declared_amount = 0.00
WHERE declared_amount IS NULL;

ALTER TABLE report_project_partner_expenditure
    ADD COLUMN currency_code VARCHAR(3) NOT NULL AFTER declared_amount,
    ADD COLUMN currency_conversion_rate  DECIMAL(15, 6) UNSIGNED   DEFAULT NULL AFTER currency_code,
    ADD COLUMN declared_amount_after_submission  DECIMAL(17, 2)   DEFAULT NULL AFTER currency_conversion_rate,
    CHANGE declared_amount declared_amount DECIMAL (17, 2) UNSIGNED NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_partner_procurement
    ADD COLUMN currency_code VARCHAR(3) NOT NULL AFTER contract_amount;
