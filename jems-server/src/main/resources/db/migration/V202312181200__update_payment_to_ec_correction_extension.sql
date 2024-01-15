ALTER TABLE payment_to_ec_correction_extension
    ADD COLUMN total_eligible_without_art_94_or_95             DECIMAL(17, 2) NOT NULL DEFAULT (fund_amount + public_contribution + auto_public_contribution + private_contribution),
    ADD COLUMN corrected_total_eligible_without_art_94_or_95   DECIMAL(17, 2) NOT NULL DEFAULT (fund_amount + public_contribution + auto_public_contribution + private_contribution),
    ADD COLUMN union_contribution                              DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN corrected_union_contribution                    DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN corrected_fund_amount                           DECIMAL(17, 2) NOT NULL DEFAULT fund_amount AFTER fund_amount;

