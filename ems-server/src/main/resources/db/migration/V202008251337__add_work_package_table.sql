CREATE TABLE work_package
(
    id                     INTEGER AUTO_INCREMENT PRIMARY KEY,
    project_id             INTEGER      NOT NULL,
    number                 INTEGER      NOT NULL,
    name                   VARCHAR(100),
    specific_objective     VARCHAR(250),
    objective_and_audience VARCHAR(500),
    CONSTRAINT fk_work_package_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
