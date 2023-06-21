ALTER TABLE report_project_partner_expenditure
    ADD COLUMN part_of_sample_locked BOOLEAN NOT NULL DEFAULT FALSE AFTER part_of_sample;

ALTER TABLE project_call
    ADD COLUMN control_report_sampling_check_plugin_key VARCHAR(255) NOT NULL DEFAULT 'control-report-sampling-check-off' AFTER report_partner_check_plugin_key;
