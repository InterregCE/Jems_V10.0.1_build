CREATE TABLE project_period
(
    project_id         INT UNSIGNED NOT NULL,
    number             INT          NOT NULL,
    start              INT          NOT NULL,
    end                INT          NOT NULL,
    PRIMARY KEY (project_id, number),
    CONSTRAINT fk_project_period_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
