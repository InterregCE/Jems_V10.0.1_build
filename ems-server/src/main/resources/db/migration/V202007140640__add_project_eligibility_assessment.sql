CREATE TABLE project_eligibility_assessment
(
    project_id INTEGER PRIMARY KEY,
    result     VARCHAR(127)  NOT NULL,
    account_id INTEGER       NOT NULL,
    updated    DATETIME      NOT NULL DEFAULT NOW(),
    note       VARCHAR(1000) NULL,
    CONSTRAINT fk_project_eligibility_assessment_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_eligibility_assessment_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);
