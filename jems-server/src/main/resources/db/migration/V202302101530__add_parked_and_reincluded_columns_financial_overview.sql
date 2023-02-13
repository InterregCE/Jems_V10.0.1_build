ALTER TABLE report_project_partner_lump_sum
    ADD COLUMN previously_reported_parked   DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER previously_paid,
    ADD COLUMN current_re_included          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER previously_reported_parked,
    ADD COLUMN current_parked               DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER current_re_included;

ALTER TABLE report_project_partner_unit_cost
    ADD COLUMN previously_reported_parked   DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER number_of_units,
    ADD COLUMN current_re_included          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER previously_reported_parked,
    ADD COLUMN current_parked               DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER current_re_included;

ALTER TABLE report_project_partner_investment
    ADD COLUMN previously_reported_parked   DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER previously_reported,
    ADD COLUMN current_re_included          DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER previously_reported_parked,
    ADD COLUMN current_parked               DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER current_re_included;

