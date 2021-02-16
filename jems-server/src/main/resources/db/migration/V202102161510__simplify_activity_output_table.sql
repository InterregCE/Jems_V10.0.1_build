ALTER TABLE project_work_package_output
    DROP CONSTRAINT fk_project_work_package_output_to_project_period,
    DROP COLUMN period_project_id,
    MODIFY COLUMN period_number SMALLINT UNSIGNED DEFAULT NULL;
