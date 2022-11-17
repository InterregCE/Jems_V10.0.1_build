SET @@system_versioning_alter_history = 1;

ALTER TABLE project_lump_sum
    ADD COLUMN payment_enabled_date DATETIME(3) DEFAULT NULL,
    ADD COLUMN last_approved_version_before_ready_for_payment VARCHAR(127) DEFAULT NULL;
