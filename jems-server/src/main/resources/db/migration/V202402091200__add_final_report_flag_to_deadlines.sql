ALTER TABLE project_contracting_reporting
    ADD COLUMN final_report     BOOLEAN NOT NULL DEFAULT FALSE AFTER deadline;


ALTER TABLE report_project
    ADD COLUMN final_report     BOOLEAN DEFAULT NULL AFTER reporting_date;

UPDATE report_project
    SET final_report = false
    WHERE deadline_id IS NULL;


