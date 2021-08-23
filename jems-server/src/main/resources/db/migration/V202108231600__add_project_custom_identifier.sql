SET @@system_versioning_alter_history = 1;
DELETE FROM project WHERE id IS NOT NULL;
ALTER TABLE project
    ADD COLUMN custom_identifier VARCHAR(31) DEFAULT NULL UNIQUE AFTER id;
