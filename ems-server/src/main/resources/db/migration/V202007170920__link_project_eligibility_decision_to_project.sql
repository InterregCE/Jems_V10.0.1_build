ALTER TABLE project
    ADD COLUMN eligibility_decision_id INTEGER DEFAULT NULL AFTER last_resubmission_id,
    ADD CONSTRAINT fk_project_eligibility_decision_project_status
        FOREIGN KEY (eligibility_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

ALTER TABLE project_status
    ADD COLUMN decision_date DATE DEFAULT NULL AFTER updated;
