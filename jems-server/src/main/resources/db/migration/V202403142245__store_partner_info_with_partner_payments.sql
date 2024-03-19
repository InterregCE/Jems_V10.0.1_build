ALTER TABLE payment_partner
    ADD COLUMN partner_abbreviation              VARCHAR(250) NOT NULL DEFAULT '',
    ADD COLUMN partner_name_in_original_language VARCHAR(250) NOT NULL DEFAULT '',
    ADD COLUMN partner_name_in_english           VARCHAR(250) NOT NULL DEFAULT '';

UPDATE payment_partner
    LEFT JOIN payment ON payment_partner.payment_id = payment.id
    LEFT JOIN project_partner ON payment_partner.partner_id = project_partner.id
SET partner_abbreviation = IF(project_partner.abbreviation IS NOT NULL, project_partner.abbreviation, ''),
    partner_name_in_original_language = IF(project_partner.name_in_original_language IS NOT NULL, project_partner.name_in_original_language, ''),
    partner_name_in_english = IF(project_partner.name_in_english IS NOT NULL, project_partner.name_in_english, '')
WHERE payment.type = 'FTLS';
