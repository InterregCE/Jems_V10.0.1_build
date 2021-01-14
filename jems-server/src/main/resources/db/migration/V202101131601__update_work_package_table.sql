ALTER TABLE project_work_package DROP COLUMN name;
ALTER TABLE project_work_package DROP COLUMN specific_objective;
ALTER TABLE project_work_package DROP COLUMN objective_and_audience;

CREATE TABLE project_work_package_transl
(
    work_package_id         INT UNSIGNED NOT NULL,
    language               VARCHAR(3)   NOT NULL,
    name                   VARCHAR(100) DEFAULT NULL,
    specific_objective     VARCHAR(250) NULL,
    objective_and_audience VARCHAR(500) NULL,
    PRIMARY KEY (work_package_id, language),
    CONSTRAINT fk_work_package_transl_to_work_package FOREIGN KEY (work_package_id) REFERENCES project_work_package (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

DROP TABLE project_work_package_output;

CREATE TABLE project_work_package_output
(
    work_package_id     INT UNSIGNED NOT NULL,
    indicator_output_id INT UNSIGNED DEFAULT NULL,
    period_project_id          INT UNSIGNED,
    period_number              SMALLINT UNSIGNED,
    output_number       TINYINT UNSIGNED NOT NULL,
    target_value        VARCHAR(5)   DEFAULT NULL,
    PRIMARY KEY (work_package_id, output_number),
    CONSTRAINT fk_project_work_package_output_to_project_work_package
        FOREIGN KEY (work_package_id)
            REFERENCES project_work_package (id)
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

CREATE TABLE project_work_package_output_transl
(
    work_package_id         INT UNSIGNED NOT NULL,
    output_number          TINYINT UNSIGNED NOT NULL,
    language               VARCHAR(3)   NOT NULL,
    title                  VARCHAR(200) DEFAULT NULL,
    description            VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (work_package_id, output_number, language),
    CONSTRAINT fk_project_work_package_output_transl_to_project_work_pkg_out
        FOREIGN KEY (work_package_id, output_number)
            REFERENCES project_work_package_output (work_package_id, output_number)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);