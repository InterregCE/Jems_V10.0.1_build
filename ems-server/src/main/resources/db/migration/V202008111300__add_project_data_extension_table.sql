CREATE TABLE project_data
(
    project_id               INTEGER PRIMARY KEY,
    title                    VARCHAR(255) DEFAULT NULL,
    duration                 INTEGER      DEFAULT NULL,
    intro                    TEXT         DEFAULT NULL,
    intro_programme_language TEXT         DEFAULT NULL,
    CONSTRAINT fk_project_data_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
