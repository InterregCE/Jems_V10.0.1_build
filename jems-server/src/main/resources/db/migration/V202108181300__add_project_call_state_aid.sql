CREATE TABLE project_call_state_aid
(
    programme_state_aid INT UNSIGNED NOT NULL,
    project_call_id        INT UNSIGNED NOT NULL,
    CONSTRAINT fk_project_call_state_aid_to_programme_state_aid
        FOREIGN KEY (programme_state_aid)
            REFERENCES programme_state_aid (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_state_aid_to_call
        FOREIGN KEY (project_call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT pk_project_call_state_aid PRIMARY KEY (programme_state_aid, project_call_id)
);
