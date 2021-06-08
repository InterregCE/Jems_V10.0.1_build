DROP TABLE project_quality_assessment;
DROP TABLE project_eligibility_assessment;
DROP TABLE project_decision;

CREATE TABLE project_assessment_quality
(
    project_id INT UNSIGNED,
    step       SMALLINT UNSIGNED NOT NULL,
    result     ENUM('RECOMMENDED_FOR_FUNDING', 'RECOMMENDED_WITH_CONDITIONS', 'NOT_RECOMMENDED') NOT NULL,
    account_id INT UNSIGNED NOT NULL,
    updated    DATETIME(3) NOT NULL DEFAULT current_timestamp (3),
    note       VARCHAR(1000) NULL,
    PRIMARY KEY (project_id, step),
    CONSTRAINT fk_project_assessment_quality_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_assessment_quality_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_assessment_eligibility
(
    project_id INT UNSIGNED,
    step       SMALLINT UNSIGNED NOT NULL,
    result     ENUM('PASSED', 'FAILED') NOT NULL,
    account_id INT UNSIGNED NOT NULL,
    updated    DATETIME(3) DEFAULT CURRENT_TIMESTAMP (3) NOT NULL,
    note       VARCHAR(1000) NULL,
    PRIMARY KEY (project_id, step),
    CONSTRAINT fk_project_assessment_eligibility_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_assessment_eligibility_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

SET @@system_versioning_alter_history = 1;

ALTER TABLE project
DROP SYSTEM VERSIONING,
DROP CONSTRAINT fk_first_step_decision_id,
DROP CONSTRAINT fk_second_step_decision_id,
DROP COLUMN step2_active,
DROP COLUMN first_step_decision_id,
DROP COLUMN second_step_decision_id,

ADD COLUMN eligibility_decision_step1_id INT UNSIGNED DEFAULT NULL
    AFTER last_resubmission_id,
ADD COLUMN funding_decision_step1_id     INT UNSIGNED DEFAULT NULL
    AFTER eligibility_decision_step1_id,
ADD COLUMN eligibility_decision_id       INT UNSIGNED DEFAULT NULL
    AFTER funding_decision_step1_id,
ADD COLUMN funding_pre_decision_id       INT UNSIGNED DEFAULT NULL
    AFTER eligibility_decision_id,
ADD COLUMN funding_final_decision_id           INT UNSIGNED DEFAULT NULL
    AFTER funding_pre_decision_id,

ADD CONSTRAINT fk_eligibility_decision_step1_to_project_status
        FOREIGN KEY (eligibility_decision_step1_id) REFERENCES project_status (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
ADD CONSTRAINT fk_funding_decision_step1_to_project_status
        FOREIGN KEY (funding_decision_step1_id) REFERENCES project_status (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
ADD CONSTRAINT fk_eligibility_decision_to_project_status
        FOREIGN KEY (eligibility_decision_id) REFERENCES project_status (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
ADD CONSTRAINT fk_funding_pre_decision_to_project_status
        FOREIGN KEY (funding_pre_decision_id) REFERENCES project_status (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
ADD CONSTRAINT fk_funding_final_decision_id_to_project_status
        FOREIGN KEY (funding_final_decision_id) REFERENCES project_status (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
ADD SYSTEM VERSIONING;
