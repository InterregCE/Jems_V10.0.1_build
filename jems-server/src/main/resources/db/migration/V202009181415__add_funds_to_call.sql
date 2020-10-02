CREATE TABLE project_call_fund
(
    programme_fund INT UNSIGNED NOT NULL,
    call_id        INT UNSIGNED NOT NULL,
    CONSTRAINT fk_project_call_fund_to_programme_fund
        FOREIGN KEY (programme_fund)
            REFERENCES programme_fund (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_fund_to_call
        FOREIGN KEY (call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT pk_project_call_fund PRIMARY KEY (programme_fund, call_id)
)