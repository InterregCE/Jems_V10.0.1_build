SET @@system_versioning_alter_history = 1;

ALTER TABLE project_work_package_transl
    DROP CONSTRAINT fk_work_package_transl_to_work_package;

ALTER TABLE project_work_package_transl
    CHANGE COLUMN work_package_id source_entity_id INT UNSIGNED NOT NULL,
    ADD CONSTRAINT fk_work_package_transl_to_work_package
        FOREIGN KEY (source_entity_id) REFERENCES project_work_package (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;
