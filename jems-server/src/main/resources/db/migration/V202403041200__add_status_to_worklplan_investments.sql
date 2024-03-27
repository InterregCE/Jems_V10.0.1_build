ALTER TABLE report_project_wp_investment ADD COLUMN status ENUM ('Finalized', 'InProgress', 'NotStarted') DEFAULT NULL;
ALTER TABLE report_project_wp_investment ADD COLUMN previous_status ENUM ('Finalized', 'InProgress', 'NotStarted') DEFAULT NULL;
