ALTER TABLE report_project_partner_expenditure_co_financing
    ADD COLUMN partner_contribution_total_eligible_after_control          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER sum_current,
    ADD COLUMN public_contribution_total_eligible_after_control           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER partner_contribution_total_eligible_after_control,
    ADD COLUMN automatic_public_contribution_total_eligible_after_control DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER public_contribution_total_eligible_after_control,
    ADD COLUMN private_contribution_total_eligible_after_control          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER automatic_public_contribution_total_eligible_after_control,
    ADD COLUMN sum_total_eligible_after_control                           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER private_contribution_total_eligible_after_control;

ALTER TABLE report_project_partner_expenditure_cost_category
    ADD COLUMN staff_total_eligible_after_control          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER sum_current,
    ADD COLUMN office_total_eligible_after_control         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER staff_total_eligible_after_control,
    ADD COLUMN travel_total_eligible_after_control         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER office_total_eligible_after_control,
    ADD COLUMN external_total_eligible_after_control       DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER travel_total_eligible_after_control,
    ADD COLUMN equipment_total_eligible_after_control      DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER external_total_eligible_after_control,
    ADD COLUMN infrastructure_total_eligible_after_control DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER equipment_total_eligible_after_control,
    ADD COLUMN other_total_eligible_after_control          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER infrastructure_total_eligible_after_control,
    ADD COLUMN lump_sum_total_eligible_after_control       DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER other_total_eligible_after_control,
    ADD COLUMN unit_cost_total_eligible_after_control      DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER lump_sum_total_eligible_after_control,
    ADD COLUMN sum_total_eligible_after_control            DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER unit_cost_total_eligible_after_control;

ALTER TABLE report_project_partner_co_financing
    ADD COLUMN total_eligible_after_control DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER `current`;

ALTER TABLE report_project_partner_lump_sum
    ADD COLUMN total_eligible_after_control DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER `current`;

ALTER TABLE report_project_partner_unit_cost
    ADD COLUMN total_eligible_after_control DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER `current`;

ALTER TABLE report_project_partner_investment
    ADD COLUMN total_eligible_after_control DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER `current`;
