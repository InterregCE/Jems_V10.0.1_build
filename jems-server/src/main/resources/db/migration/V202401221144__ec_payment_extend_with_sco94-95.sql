ALTER TABLE payment_to_ec_extension
    ADD COLUMN  corrected_total_eligible_without_sco DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN  corrected_fund_amount_union_contribution  DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN  corrected_fund_amount_public_contribution  DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

-- update existing payment extensions
UPDATE payment_to_ec_extension pmec
    INNER JOIN
    (select p.id                                                     AS paymentId,
            (p.amount_approved_per_fund + pmec.partner_contribution) AS totalEligibleWSco,
            p.amount_approved_per_fund                               AS amountApprovedPerFund
     from payment p
              inner join payment_to_ec_extension pmec on p.id = pmec.payment_id
              inner join project_contracting_monitoring pcm on pcm.project_id = p.project_id
     where (pcm.typology_prov_94 is not null and pcm.typology_prov_94 <> 'No') AND
         (pcm.typology_prov_95 is not null and pcm.typology_prov_95 <> 'No')
    )
        as paymentsFlagged9495 on paymentsFlagged9495.paymentId = pmec.payment_id
SET corrected_total_eligible_without_sco      = totalEligibleWSco,
    corrected_fund_amount_public_contribution = amountApprovedPerFund;
