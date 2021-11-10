SET @@system_versioning_alter_history = 1;

ALTER TABLE project_work_package_activity_deliverable_transl
    MODIFY COLUMN description TEXT(300) DEFAULT NULL,
    ADD COLUMN title          VARCHAR(100) DEFAULT NULL
