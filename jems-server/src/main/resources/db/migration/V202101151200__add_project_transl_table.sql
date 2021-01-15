CREATE TABLE project_transl
(
    project_id INT UNSIGNED NOT NULL,
    language   VARCHAR(3)   NOT NULL,
    title      VARCHAR(200),
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_transl_to_project
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project
    DROP COLUMN title;