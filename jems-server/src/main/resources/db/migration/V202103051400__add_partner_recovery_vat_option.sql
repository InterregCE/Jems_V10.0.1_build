ALTER TABLE project_partner DROP COLUMN vat_recovery;
ALTER TABLE project_partner ADD COLUMN vat_recovery ENUM('Yes', 'Partly', 'No');