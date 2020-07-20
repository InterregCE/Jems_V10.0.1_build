ALTER TABLE project
    ADD COLUMN funding_decision_id INTEGER DEFAULT NULL AFTER eligibility_decision_id,
    ADD CONSTRAINT fk_project_funding_decision_project_status
        FOREIGN KEY (funding_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;
