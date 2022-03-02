SET @@system_versioning_alter_history = 1;

ALTER TABLE project_call
    ADD COLUMN first_step_pre_submission_check_plugin_key VARCHAR(255) DEFAULT NULL;

UPDATE project_call
SET first_step_pre_submission_check_plugin_key = 'jems-pre-condition-check-off'
WHERE end_date_step1 IS NOT NULL
