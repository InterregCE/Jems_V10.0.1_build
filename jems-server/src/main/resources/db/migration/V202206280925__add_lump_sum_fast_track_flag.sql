ALTER TABLE programme_lump_sum
    ADD COLUMN is_fast_track BOOLEAN NOT NULL DEFAULT FALSE AFTER splitting_allowed;
