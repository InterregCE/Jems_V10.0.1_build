ALTER TABLE project
    ADD COLUMN project_call_id INTEGER NOT NULL AFTER id,
    ADD CONSTRAINT fk_project_project_call FOREIGN KEY (project_call_id) REFERENCES project_call (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
