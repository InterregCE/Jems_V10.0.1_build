SET @@system_versioning_alter_history = 1;

ALTER TABLE project_work_package
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE project_work_package_investment
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE project_work_package_activity
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE project_work_package_activity_deliverable
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE project_work_package_output
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE report_project_partner_wp_output
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE report_project_partner_investment
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE AFTER work_package_number;
