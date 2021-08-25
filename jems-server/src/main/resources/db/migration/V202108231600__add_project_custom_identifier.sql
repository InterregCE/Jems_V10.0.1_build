SET @@system_versioning_alter_history = 1;
ALTER TABLE project
    ADD COLUMN custom_identifier VARCHAR(31) DEFAULT NULL UNIQUE AFTER id;

-- insert id for old values to keep old test data, 'PREC0000id'
update project set custom_identifier = CONCAT('PREC', LPAD(id, 6, 0))
    WHERE id is not NULL;

ALTER TABLE programme_data
    ADD COLUMN project_id_programme_abbreviation VARCHAR(31) DEFAULT NULL
    AFTER programme_amending_decision_date,
    ADD COLUMN project_id_use_call_id BOOLEAN NOT NULL DEFAULT FALSE
        AFTER programme_amending_decision_date;
