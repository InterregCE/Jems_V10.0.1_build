ALTER TABLE project
    ADD COLUMN applicant_id INTEGER NOT NULL AFTER acronym,
    ADD CONSTRAINT fk_applicant_user FOREIGN KEY (applicant_id) REFERENCES account (id);

ALTER TABLE project_file
    ADD COLUMN author_id INTEGER NOT NULL AFTER project_id,
    ADD CONSTRAINT fk_author_user FOREIGN KEY (author_id) REFERENCES account (id);
