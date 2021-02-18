ALTER TABLE project_work_package_output
    DROP CONSTRAINT fk_project_work_package_output_to_project_period,
    DROP COLUMN period_project_id,
    MODIFY COLUMN period_number SMALLINT UNSIGNED DEFAULT NULL;

DROP TABLE project_result_transl;
DROP TABLE project_result;

CREATE TABLE project_result
(
    project_id          INT UNSIGNED     NOT NULL,
    result_number       TINYINT UNSIGNED NOT NULL,
    period_number       SMALLINT UNSIGNED,
    indicator_result_id INT UNSIGNED   DEFAULT NULL,
    target_value        DECIMAL(11, 2) DEFAULT NULL,
    PRIMARY KEY (project_id, result_number),
    CONSTRAINT fk_project_result_to_project
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_result_indicator_to_programme_indicator_result
        FOREIGN KEY (indicator_result_id) REFERENCES programme_indicator_result (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

CREATE TABLE project_result_transl
(
    project_id    INT UNSIGNED     NOT NULL,
    result_number TINYINT UNSIGNED NOT NULL,
    language      VARCHAR(3)       NOT NULL,
    description   TEXT(500) DEFAULT NULL,
    PRIMARY KEY (project_id, result_number, language),
    CONSTRAINT fk_project_result_transl_to_project_result FOREIGN KEY (project_id, result_number) REFERENCES project_result (project_id, result_number)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_output
    MODIFY COLUMN target_value DECIMAL(11, 2) DEFAULT NULL;
