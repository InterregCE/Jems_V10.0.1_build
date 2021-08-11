ALTER TABLE programme_state_aid
    DROP COLUMN max_intensity,
    DROP COLUMN threshold,
    ADD COLUMN max_intensity DECIMAL(5, 2) UNSIGNED DEFAULT NULL AFTER scheme_number,
    ADD COLUMN threshold DECIMAL(17, 2) UNSIGNED DEFAULT NULL AFTER max_intensity;
