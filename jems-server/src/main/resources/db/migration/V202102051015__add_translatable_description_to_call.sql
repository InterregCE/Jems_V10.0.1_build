ALTER TABLE project_call
    DROP COLUMN description;

CREATE TABLE project_call_transl
(
    project_call_id INT UNSIGNED NOT NULL,
    language        VARCHAR(3)   NOT NULL,
    description     TEXT(1000) DEFAULT NULL,
    PRIMARY KEY (project_call_id, language),
    CONSTRAINT fk_project_call_transl_to_project_call
        FOREIGN KEY (project_call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
)
