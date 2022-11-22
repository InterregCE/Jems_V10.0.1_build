ALTER TABLE project_call
    ADD COLUMN report_partner_check_plugin_key VARCHAR(255) NOT NULL DEFAULT 'report-partner-check-off' AFTER first_step_pre_submission_check_plugin_key;
