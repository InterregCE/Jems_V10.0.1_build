CREATE TABLE project_result
(
    id                  BINARY(16) PRIMARY KEY NOT NULL,
    project_id          INT UNSIGNED           NOT NULL,
    indicator_result_id INT UNSIGNED DEFAULT NULL,
    period_project_id          INT UNSIGNED,
    period_number              SMALLINT UNSIGNED,
    result_number       INT          DEFAULT NULL,
    target_value        VARCHAR(5)   DEFAULT NULL,
    CONSTRAINT fk_project_result_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_result_to_programme_indicator_result
        FOREIGN KEY (indicator_result_id) REFERENCES programme_indicator_result (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_result_to_project_period
        FOREIGN KEY (period_project_id, period_number) REFERENCES project_period (project_id, number)
            ON DELETE SET NULL
            ON UPDATE SET NULL
);
