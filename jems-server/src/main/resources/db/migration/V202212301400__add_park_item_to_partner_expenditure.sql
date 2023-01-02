ALTER TABLE report_project_partner_expenditure
    ADD COLUMN parked BOOLEAN NOT NULL DEFAULT FALSE AFTER typology_of_error_id,
    ADD COLUMN number INT UNSIGNED NOT NULL AFTER id;

UPDATE report_project_partner_expenditure rppe
INNER JOIN(
    SELECT id, DENSE_RANK() OVER (PARTITION BY partner_report_id ORDER BY id ASC) as position
    FROM report_project_partner_expenditure
) rank_table
ON rppe.id = rank_table.id
SET rppe.number = rank_table.position
