SET @@system_versioning_alter_history = 1;
ALTER TABLE project_result
    ADD COLUMN baseline DECIMAL(11, 2) DEFAULT 0;
