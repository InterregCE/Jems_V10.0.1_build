ALTER TABLE report_project_partner_unit_cost
    ADD COLUMN previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported;

ALTER TABLE report_project_partner_investment
    ADD COLUMN previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported;

ALTER TABLE report_project_partner_lump_sum
    ADD COLUMN previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported;

ALTER TABLE report_project_partner_expenditure_cost_category
    ADD COLUMN staff_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER sum_previously_reported,
    ADD COLUMN office_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER staff_previously_validated,
    ADD COLUMN travel_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER office_previously_validated,
    ADD COLUMN external_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER travel_previously_validated,
    ADD COLUMN equipment_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER external_previously_validated,
    ADD COLUMN infrastructure_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER equipment_previously_validated,
    ADD COLUMN other_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER infrastructure_previously_validated,
    ADD COLUMN lump_sum_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER other_previously_validated,
    ADD COLUMN unit_cost_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER lump_sum_previously_validated,
    ADD COLUMN sum_previously_validated DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_previously_validated;



