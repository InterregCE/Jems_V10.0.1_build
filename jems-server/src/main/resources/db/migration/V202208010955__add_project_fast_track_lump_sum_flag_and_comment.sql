SET @@system_versioning_alter_history = 1;
ALTER TABLE project_lump_sum
    ADD COLUMN is_ready_for_payment BOOLEAN NOT NULL DEFAULT FALSE AFTER end_period,
    ADD COLUMN comment TEXT(200) AFTER is_ready_for_payment;
