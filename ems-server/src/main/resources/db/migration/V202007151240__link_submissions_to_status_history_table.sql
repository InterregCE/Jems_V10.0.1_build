ALTER TABLE project
    DROP COLUMN submission_date,
    DROP COLUMN resubmission_date,
    ADD COLUMN first_submission_id  INTEGER DEFAULT NULL AFTER project_status_id,
    ADD CONSTRAINT fk_project_first_submission_project_status
        FOREIGN KEY (first_submission_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD COLUMN last_resubmission_id INTEGER DEFAULT NULL AFTER first_submission_id,
    ADD CONSTRAINT fk_project_last_resubmission_project_status
        FOREIGN KEY (last_resubmission_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;
