ALTER TABLE project_contracting_partner_banking_details
    ADD COLUMN internal_reference_nr VARCHAR(50) DEFAULT NULL AFTER account_swift_bic_code,
    ADD COLUMN city                  VARCHAR(50) DEFAULT NULL AFTER postal_code;
