ALTER TABLE payment_partner
    ADD COLUMN partner_abbreviation              VARCHAR(250) NOT NULL DEFAULT '',
    ADD COLUMN partner_name_in_original_language VARCHAR(250) NOT NULL DEFAULT '',
    ADD COLUMN partner_name_in_english           VARCHAR(250) NOT NULL DEFAULT '';

UPDATE payment_partner
    LEFT JOIN payment ON payment_partner.payment_id = payment.id
    LEFT JOIN project_partner ON payment_partner.partner_id = project_partner.id
SET partner_abbreviation = project_partner.abbreviation,
    partner_name_in_original_language = project_partner.name_in_original_language,
    partner_name_in_english = project_partner.name_in_english
WHERE payment.type = 'FTLS';
