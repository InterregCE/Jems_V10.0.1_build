ALTER TABLE programme_unit_cost
    ADD COLUMN cost_per_unit_foreign_currency DECIMAL(11, 2) DEFAULT NULL,
    ADD COLUMN foreign_currency_code VARCHAR(3) DEFAULT NULL AFTER cost_per_unit_foreign_currency;
