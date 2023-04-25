ALTER TABLE project_call
    ADD COLUMN control_report_partner_check_plugin_key VARCHAR(255) NOT NULL DEFAULT 'control-report-partner-check-off' AFTER report_partner_check_plugin_key;
