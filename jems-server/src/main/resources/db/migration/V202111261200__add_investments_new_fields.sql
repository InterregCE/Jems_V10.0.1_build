SET @@system_versioning_alter_history = 1;
ALTER TABLE project_work_package_investment
    ADD COLUMN expected_delivery_period SMALLINT UNSIGNED DEFAULT NULL;
ALTER TABLE project_work_package_investment_transl
    ADD COLUMN documentation_expected_impacts TEXT(2000) AFTER documentation;