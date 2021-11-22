SET @@system_versioning_alter_history = 1;
ALTER TABLE project
    ADD COLUMN modification_decision_id INT UNSIGNED DEFAULT NULL AFTER funding_final_decision_id,

    ADD CONSTRAINT fk_modification_decision_id_to_project_status
        FOREIGN KEY (modification_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

ALTER TABLE project_status
    ADD COLUMN entry_into_force_date DATE NULL;

