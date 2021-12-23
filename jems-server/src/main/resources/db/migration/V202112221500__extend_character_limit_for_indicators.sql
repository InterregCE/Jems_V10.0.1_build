SET @@system_versioning_alter_history = 1;
ALTER TABLE programme_indicator_output
    MODIFY identifier VARCHAR(10) NOT NULL;

ALTER TABLE programme_indicator_result
    MODIFY identifier VARCHAR(10) NOT NULL;