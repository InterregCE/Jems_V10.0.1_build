
ALTER TABLE report_project_partner
    ADD COLUMN country   VARCHAR(100) DEFAULT NULL AFTER vat_recovery,
    ADD COLUMN currency  VARCHAR(3)   DEFAULT NULL AFTER country;
