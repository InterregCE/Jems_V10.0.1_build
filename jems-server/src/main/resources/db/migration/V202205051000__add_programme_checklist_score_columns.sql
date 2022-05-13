ALTER TABLE programme_checklist
    ADD COLUMN min_score DECIMAL(7, 2) UNSIGNED AFTER name,
    ADD COLUMN max_score DECIMAL(7, 2) UNSIGNED AFTER min_score,
    ADD COLUMN allows_decimal_score BOOLEAN NOT NULL DEFAULT false after max_score;
