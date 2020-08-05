CREATE TABLE project_call_priority_policy (
    programme_priority_policy VARCHAR(127) NOT NULL,
    call_id INTEGER NOT NULL,
    CONSTRAINT fk_call_priority_policy_programme_priority_policy
        FOREIGN KEY (programme_priority_policy)
        REFERENCES programme_priority_policy (programme_objective_policy_code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_call_priority_policy_call
        FOREIGN KEY (call_id)
        REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT pk_project_call_priority_policy PRIMARY KEY (programme_priority_policy, call_id)
);
