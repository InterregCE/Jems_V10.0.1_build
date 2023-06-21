ALTER TABLE project_call
    ADD COLUMN report_project_check_plugin_key VARCHAR(255) NOT NULL DEFAULT 'report-project-check-off' AFTER report_partner_check_plugin_key;
