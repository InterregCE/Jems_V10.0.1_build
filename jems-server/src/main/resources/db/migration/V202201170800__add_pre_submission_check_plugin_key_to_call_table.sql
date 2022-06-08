SET @@system_versioning_alter_history = 1;

ALTER TABLE project_call
    ADD COLUMN pre_submission_check_plugin_key VARCHAR(255) DEFAULT NULL;

UPDATE project_call
SET pre_submission_check_plugin_key = 'standard-pre-condition-check-plugin'
WHERE status = 'PUBLISHED'
