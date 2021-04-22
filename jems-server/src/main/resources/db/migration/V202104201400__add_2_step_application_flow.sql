DROP TABLE project_quality_assessment;
DROP TABLE project_eligibility_assessment;

ALTER TABLE project
    DROP CONSTRAINT fk_project_eligibility_decision_project_status,
    DROP CONSTRAINT fk_project_funding_decision_project_status;

CREATE TABLE project_decision
(
    id                      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id              INT UNSIGNED NULL,
    eligibility_decision_id INT UNSIGNED DEFAULT NULL,
    funding_decision_id     INT UNSIGNED DEFAULT NULL,
    CONSTRAINT fk_project_eligibility_decision_project_status
        FOREIGN KEY (eligibility_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_funding_decision_project_status
        FOREIGN KEY (funding_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_decision_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT

);

CREATE TABLE project_eligibility_assessment
(
    project_decision_id INT UNSIGNED PRIMARY KEY,
    result              VARCHAR(127)                             NOT NULL,
    account_id          INT UNSIGNED                             NOT NULL,
    updated             DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    note                VARCHAR(1000)                            NULL,
    CONSTRAINT fk_project_eligibility_assessment_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_eligibility_assessment_project
        FOREIGN KEY (project_decision_id) REFERENCES project_decision (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);


CREATE TABLE project_quality_assessment
(
    project_decision_id INT UNSIGNED PRIMARY KEY,
    result              VARCHAR(127)  NOT NULL,
    account_id          INT UNSIGNED  NOT NULL,
    updated             DATETIME(3)   NOT NULL DEFAULT current_timestamp(3),
    note                VARCHAR(1000) NULL,
    CONSTRAINT fk_project_quality_assessment_to_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_quality_assessment_to_project
        FOREIGN KEY (project_decision_id) REFERENCES project_decision (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project
    ADD COLUMN step2_active            BOOLEAN NOT NULL,
    DROP COLUMN eligibility_decision_id,
    DROP COLUMN funding_decision_id,
    ADD COLUMN first_step_decision_id  INT UNSIGNED DEFAULT NULL,
    ADD COLUMN second_step_decision_id INT UNSIGNED DEFAULT NULL,
    ADD CONSTRAINT fk_first_step_decision_id
        FOREIGN KEY (first_step_decision_id) REFERENCES project_decision (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_second_step_decision_id
        FOREIGN KEY (second_step_decision_id) REFERENCES project_decision (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;
