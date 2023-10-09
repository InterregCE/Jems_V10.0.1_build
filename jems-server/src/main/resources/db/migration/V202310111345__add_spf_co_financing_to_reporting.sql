ALTER TABLE report_project_partner_co_financing
    ADD COLUMN percentage_spf DECIMAL(11, 2) NOT NULL DEFAULT 0 AFTER percentage;

UPDATE report_project_partner_co_financing
SET percentage_spf = 100
WHERE programme_fund_id IS NULL;

ALTER TABLE report_project_partner_expenditure_cost_category
    ADD COLUMN spf_cost_total DECIMAL(17, 2) NOT NULL DEFAULT 0 AFTER unit_cost_total;

ALTER TABLE report_project_certificate_cost_category
    ADD COLUMN spf_cost_total DECIMAL(17, 2) NOT NULL DEFAULT 0 AFTER unit_cost_total;
