CREATE TABLE project_work_package_output
(
    id                  BINARY(16) PRIMARY KEY NOT NULL,
    work_package_id     INT UNSIGNED           NOT NULL,
    indicator_output_id INT UNSIGNED DEFAULT NULL,
    period_project_id          INT UNSIGNED,
    period_number              SMALLINT UNSIGNED,
    output_number       INT          DEFAULT NULL,
    title               VARCHAR(200) DEFAULT NULL,
    target_value        VARCHAR(5)   DEFAULT NULL,
    description         VARCHAR(500) DEFAULT NULL,
    CONSTRAINT fk_project_work_package_output_project_work_package
        FOREIGN KEY (work_package_id) REFERENCES project_work_package (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_work_package_output_to_programme_indicator_output
        FOREIGN KEY (indicator_output_id) REFERENCES programme_indicator_output (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_work_package_output_to_project_period
        FOREIGN KEY (period_project_id, period_number) REFERENCES project_period (project_id, number)
            ON DELETE SET NULL
            ON UPDATE SET NULL
);