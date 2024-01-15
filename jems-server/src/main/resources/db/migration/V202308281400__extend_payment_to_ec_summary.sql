ALTER TABLE payment_applications_to_ec
    ADD COLUMN national_reference VARCHAR(50),
    ADD COLUMN technical_assistance_eur DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN submission_to_sfc_date  DATETIME(3)  DEFAULT NULL,
    ADD COLUMN sfc_number VARCHAR(50) DEFAULT NULL,
    ADD COLUMN comment  TEXT(5000) DEFAULT NULL;



