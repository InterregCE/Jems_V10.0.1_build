DROP TABLE report_project_partner_procurement_transl;

ALTER TABLE report_project_partner_procurement
    DROP FOREIGN KEY fk_report_procurement_to_report_file,
    DROP COLUMN file_id,
    DROP COLUMN contract_id,
    ADD COLUMN contract_name VARCHAR(50) NOT NULL DEFAULT '' AFTER report_id,
    ADD COLUMN reference_number VARCHAR(30) NOT NULL DEFAULT '' AFTER contract_name,
    ADD COLUMN contract_date DATE DEFAULT NULL AFTER reference_number,
    ADD COLUMN contract_type VARCHAR(30) NOT NULL DEFAULT '' AFTER contract_date,
    ADD COLUMN vat_number VARCHAR(30) NOT NULL DEFAULT '' AFTER supplier_name,
    ADD COLUMN comment TEXT(2000) NOT NULL DEFAULT '' AFTER vat_number,
    ADD COLUMN last_changed DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) AFTER comment;
