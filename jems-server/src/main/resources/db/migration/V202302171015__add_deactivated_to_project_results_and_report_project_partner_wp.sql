SET @@system_versioning_alter_history = 1;

ALTER TABLE project_result
ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE report_project_partner_wp
    ADD COLUMN deactivated BOOLEAN NOT NULL DEFAULT FALSE;
