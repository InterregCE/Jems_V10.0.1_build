DROP TABLE IF EXISTS project_data;

DELETE
FROM project
WHERE id IS NOT NULL;

ALTER TABLE project
    ADD COLUMN programme_priority_policy_objective_policy VARCHAR(127) DEFAULT NULL AFTER project_call_id,
    ADD COLUMN title                                      VARCHAR(255) DEFAULT NULL AFTER funding_decision_id,
    ADD COLUMN duration                                   INTEGER      DEFAULT NULL AFTER title,
    ADD COLUMN intro                                      TEXT         DEFAULT NULL AFTER duration,
    ADD COLUMN intro_programme_language                   TEXT         DEFAULT NULL AFTER intro,
    ADD CONSTRAINT fk_project_to_programme_priority_policy
        FOREIGN KEY (programme_priority_policy_objective_policy)
            REFERENCES programme_priority_policy (programme_objective_policy_code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;
