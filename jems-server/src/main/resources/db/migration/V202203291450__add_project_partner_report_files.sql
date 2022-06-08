CREATE TABLE report_project_file
(
    id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id     INT UNSIGNED DEFAULT NULL,
    partner_id     INT UNSIGNED DEFAULT NULL,
    path           VARCHAR(2048) NOT NULL,
    minio_bucket   VARCHAR(255)  NOT NULL,
    minio_location VARCHAR(2048) NOT NULL,
    name           VARCHAR(255)  NOT NULL,
    type           ENUM('Project', 'Report', 'WorkPlan', 'Activity', 'Deliverable', 'Output', 'Expenditure', 'Procurement', 'Contribution') NOT NULL,
    size           INT UNSIGNED NOT NULL,
    account_id     INT UNSIGNED DEFAULT NULL,
    uploaded       DATETIME(3) DEFAULT CURRENT_TIMESTAMP (3) NOT NULL,
    CONSTRAINT fk_report_file_to_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_file_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_file_to_project_partner
        FOREIGN KEY (partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    KEY( path)
);

ALTER TABLE report_project_partner_wp_activity
    ADD COLUMN file_id INT UNSIGNED DEFAULT NULL AFTER activity_id,
    ADD CONSTRAINT fk_report_wp_activity_to_report_file
        FOREIGN KEY(file_id) REFERENCES report_project_file(id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;

ALTER TABLE report_project_partner_wp_activity_deliverable
    ADD COLUMN file_id INT UNSIGNED DEFAULT NULL AFTER deliverable_id,
    ADD CONSTRAINT fk_report_wp_activity_deliverable_to_report_file
        FOREIGN KEY(file_id) REFERENCES report_project_file(id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;

ALTER TABLE report_project_partner_wp_output
    ADD COLUMN file_id INT UNSIGNED DEFAULT NULL AFTER evidence,
    ADD CONSTRAINT fk_report_wp_output_to_report_file
        FOREIGN KEY(file_id) REFERENCES report_project_file(id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT;
