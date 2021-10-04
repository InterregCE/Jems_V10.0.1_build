ALTER TABLE project_call_fund
    ADD COLUMN rate          DECIMAL(5, 2) UNSIGNED NOT NULL DEFAULT 100,
    ADD COLUMN is_adjustable BOOLEAN                NOT NULL DEFAULT true
