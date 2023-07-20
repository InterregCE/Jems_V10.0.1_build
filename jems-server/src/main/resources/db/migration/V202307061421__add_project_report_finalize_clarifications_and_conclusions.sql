ALTER TABLE report_project
    CHANGE COLUMN status status ENUM ('Draft', 'Submitted', 'InVerification', 'Finalized') NOT NULL DEFAULT 'Draft',
    ADD COLUMN verification_conclusion_js   TEXT(5000)   DEFAULT NULL AFTER verification_end_date,
    ADD COLUMN verification_conclusion_ma   TEXT(5000)   DEFAULT NULL AFTER verification_conclusion_js,
    ADD COLUMN verification_followup        TEXT(5000)   DEFAULT NULL AFTER verification_conclusion_ma;


CREATE TABLE report_project_verification_clarification(
    id                      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    number                  INT NOT NULL,
    project_report_id       INT UNSIGNED NOT NULL,
    request_date            DATETIME(3) NOT NULL,
    answer_date             DATETIME(3),
    comment                 TEXT(3000) NOT NULL,

    CONSTRAINT fk_verification_clarification_to_project_report FOREIGN KEY (project_report_id) REFERENCES report_project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);