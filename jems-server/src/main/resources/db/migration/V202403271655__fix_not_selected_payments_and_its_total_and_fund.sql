UPDATE payment_to_ec_extension
SET corrected_total_eligible_without_sco      = total_eligible_without_sco,
    corrected_fund_amount_public_contribution = fund_amount_public_contribution
WHERE payment_application_to_ec_id IS NULL;
