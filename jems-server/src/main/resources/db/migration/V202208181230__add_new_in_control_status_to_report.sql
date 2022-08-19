ALTER TABLE report_project_partner
    CHANGE COLUMN status status ENUM ('Draft', 'Submitted', 'InControl') DEFAULT 'Draft';
