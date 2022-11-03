SET @@system_versioning_alter_history = 1;

ALTER TABLE project_work_package_transl
    MODIFY COLUMN specific_objective VARCHAR(1000) NULL,
    MODIFY COLUMN objective_and_audience VARCHAR(1000) NULL;

ALTER TABLE project_work_package_activity_transl
    MODIFY description TEXT(3000) DEFAULT NULL;

ALTER TABLE project_work_package_investment_transl
    MODIFY COLUMN ownership_site_location TEXT(2000),
    MODIFY COLUMN ownership_retain TEXT(2000);
