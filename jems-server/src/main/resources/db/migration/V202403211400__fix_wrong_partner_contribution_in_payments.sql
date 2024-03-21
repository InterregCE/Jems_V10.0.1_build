ALTER TABLE payment_to_ec_extension
    ADD COLUMN before_fix_partner_contribution DECIMAL(17, 2) DEFAULT NULL;

UPDATE payment_to_ec_extension ptee
    LEFT JOIN payment p ON ptee.payment_id = p.id
SET ptee.before_fix_partner_contribution = partner_contribution
WHERE p.type = 'FTLS';
