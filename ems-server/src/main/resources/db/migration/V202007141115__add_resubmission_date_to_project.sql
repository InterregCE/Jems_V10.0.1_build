ALTER TABLE project
    ADD COLUMN resubmission_date DATETIME(3) DEFAULT NULL AFTER submission_date,
    MODIFY submission_date DATETIME(3) NULL;

ALTER TABLE project_status
    MODIFY updated DATETIME(3) NOT NULL DEFAULT NOW();
