ALTER TABLE report_project_partner_wp
    DROP FOREIGN KEY fk_report_wp_to_work_package;

ALTER TABLE report_project_partner_wp_activity
    DROP FOREIGN KEY fk_report_partner_wp_activity_to_wp_activity;

ALTER TABLE report_project_partner_wp_activity_deliverable
    DROP FOREIGN KEY fk_report_wp_deliverable_to_wp_activity_deliverable;
