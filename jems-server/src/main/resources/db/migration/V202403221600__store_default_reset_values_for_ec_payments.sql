ALTER TABLE payment_to_ec_extension
    ADD COLUMN total_eligible_without_sco      DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER comment,
    ADD COLUMN fund_amount_union_contribution  DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER corrected_total_eligible_without_sco,
    ADD COLUMN fund_amount_public_contribution DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER corrected_fund_amount_union_contribution;

UPDATE payment_to_ec_extension ptee
    LEFT JOIN payment p ON ptee.payment_id = p.id
SET
    ptee.total_eligible_without_sco      = p.amount_approved_per_fund + ptee.partner_contribution,
    ptee.fund_amount_public_contribution = p.amount_approved_per_fund;
