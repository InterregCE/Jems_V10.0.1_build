SET @@system_versioning_alter_history = 1;

ALTER TABLE report_project_partner_wp_activity
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE report_project_partner_wp_activity_deliverable
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;
