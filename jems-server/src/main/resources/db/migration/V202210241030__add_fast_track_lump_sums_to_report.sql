ALTER TABLE report_project_partner_lump_sum
    ADD COLUMN previously_paid DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER previously_reported;
