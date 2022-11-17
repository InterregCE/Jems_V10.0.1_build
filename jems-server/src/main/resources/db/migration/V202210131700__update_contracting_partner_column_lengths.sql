ALTER TABLE project_contracting_partner_beneficial_owner
    CHANGE COLUMN vat_number vat_number VARCHAR(50) NOT NULL DEFAULT '';

ALTER TABLE project_contracting_partner_banking_details
    CHANGE COLUMN street_number street_number VARCHAR(20) DEFAULT '',
    CHANGE COLUMN postal_code postal_code  VARCHAR(20) DEFAULT '';

ALTER TABLE project_contracting_partner_documents_location
    CHANGE COLUMN homepage homepage VARCHAR(250) DEFAULT '';
