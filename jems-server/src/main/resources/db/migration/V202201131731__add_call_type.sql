SET @@system_versioning_alter_history = 1;

ALTER TABLE project_call
    ADD COLUMN type VARCHAR(10) NOT NULL AFTER status;

UPDATE project_call set type = 'STANDARD' WHERE type = '';
