SET @@system_versioning_alter_history = 1;
ALTER TABLE project_work_package_activity_transl
MODIFY description TEXT(1000)    DEFAULT NULL;

ALTER TABLE project_result_transl
MODIFY description TEXT(1000)    DEFAULT NULL;
