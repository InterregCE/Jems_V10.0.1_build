RENAME TABLE report_project_file TO file_metadata;


ALTER TABLE report_project_partner_wp_activity
DROP CONSTRAINT fk_report_wp_activity_to_report_file;

ALTER TABLE report_project_partner_wp_activity
ADD CONSTRAINT fk_report_wp_activity_to_file_metadata
    FOREIGN KEY(file_id) REFERENCES file_metadata(id)
     ON DELETE SET NULL
     ON UPDATE RESTRICT;



ALTER TABLE report_project_partner_wp_activity_deliverable
DROP CONSTRAINT fk_report_wp_activity_deliverable_to_report_file;

ALTER TABLE report_project_partner_wp_activity_deliverable
ADD CONSTRAINT fk_report_wp_activity_deliverable_to_file_metadata
    FOREIGN KEY(file_id) REFERENCES file_metadata(id)
     ON DELETE SET NULL
     ON UPDATE RESTRICT;



ALTER TABLE report_project_partner_wp_output
DROP CONSTRAINT fk_report_wp_output_to_report_file;

ALTER TABLE report_project_partner_wp_output
ADD CONSTRAINT fk_report_wp_output_to_file_metadata
    FOREIGN KEY(file_id) REFERENCES file_metadata(id)
     ON DELETE SET NULL
     ON UPDATE RESTRICT;



ALTER TABLE report_project_partner_procurement_file
DROP CONSTRAINT fk_procurement_file_to_file;

ALTER TABLE report_project_partner_procurement_file
ADD CONSTRAINT fk_report_procurement_to_file_metadata
    FOREIGN KEY(file_id) REFERENCES file_metadata(id)
     ON DELETE CASCADE
     ON UPDATE RESTRICT;



ALTER TABLE report_project_partner_contribution
DROP CONSTRAINT fk_report_contribution_to_report_file;

ALTER TABLE report_project_partner_contribution
ADD CONSTRAINT fk_report_contribution_to_file_metadata
    FOREIGN KEY(file_id) REFERENCES file_metadata(id)
     ON DELETE SET NULL
     ON UPDATE RESTRICT;



ALTER TABLE report_project_partner_expenditure
DROP CONSTRAINT fk_report_expenditure_to_report_file;

ALTER TABLE report_project_partner_expenditure
ADD CONSTRAINT fk_report_expenditure_to_file_metadata
    FOREIGN KEY(file_id) REFERENCES file_metadata(id)
     ON DELETE SET NULL
     ON UPDATE RESTRICT;