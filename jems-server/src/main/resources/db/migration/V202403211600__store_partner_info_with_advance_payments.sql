ALTER TABLE payment_advance
    ADD COLUMN partner_name_in_original_language VARCHAR(250) DEFAULT NULL AFTER partner_abbreviation,
    ADD COLUMN partner_name_in_english           VARCHAR(250) DEFAULT NULL AFTER partner_name_in_original_language;

UPDATE payment_advance
    LEFT JOIN project_partner ON payment_advance.partner_id = project_partner.id
    SET partner_name_in_original_language = project_partner.name_in_original_language,
        partner_name_in_english = project_partner.name_in_english;
