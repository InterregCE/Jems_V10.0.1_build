ALTER TABLE payment_account_correction_extension
    DROP COLUMN comment,
    DROP COLUMN final_sco_basis;
ALTER TABLE payment_account_correction_extension
    ADD COLUMN comment VARCHAR(500) NOT NULL DEFAULT '';
