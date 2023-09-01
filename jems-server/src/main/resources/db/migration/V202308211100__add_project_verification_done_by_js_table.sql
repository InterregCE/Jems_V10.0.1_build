CREATE TABLE report_project_verification_notification
(
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_report_id INT UNSIGNED,
    user_id           INT UNSIGNED NOT NULL,
    created_at        DATETIME(3) DEFAULT CURRENT_TIMESTAMP (3) NOT NULL,
    CONSTRAINT fk_verification_notification_to_report_project
        FOREIGN KEY (project_report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_verification_notification_to_account
        FOREIGN KEY (user_id) REFERENCES account (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
