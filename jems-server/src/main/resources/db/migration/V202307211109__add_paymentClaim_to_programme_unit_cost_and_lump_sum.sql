SET @@system_versioning_alter_history = 1;

ALTER TABLE programme_unit_cost
    ADD COLUMN payment_claim varchar(50) NOT NULL DEFAULT 'IncurredByBeneficiaries' AFTER foreign_currency_code;
ALTER TABLE programme_lump_sum
    ADD COLUMN payment_claim varchar(50) NOT NULL DEFAULT 'IncurredByBeneficiaries' AFTER phase;
