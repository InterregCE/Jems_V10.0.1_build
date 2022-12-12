ALTER TABLE report_project_partner
    CHANGE COLUMN status status ENUM ('Draft', 'Submitted', 'InControl', 'Certified') DEFAULT 'Draft',
    ADD COLUMN control_end DATETIME(3) DEFAULT NULL AFTER first_submission;
