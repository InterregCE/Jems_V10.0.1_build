ALTER TABLE payment_to_ec_extension
    ADD COLUMN comment VARCHAR(500) DEFAULT NULL AFTER corrected_private_contribution;
