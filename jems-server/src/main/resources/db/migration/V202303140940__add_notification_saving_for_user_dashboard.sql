CREATE TABLE call_notification
(
    id                       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id                  INT UNSIGNED NOT NULL,
    project_id               INT UNSIGNED NOT NULL,
    created                  DATETIME(3) NOT NULL,
    subject                  TEXT(255) NOT NULL DEFAULT '',
    body                     TEXT(10000) NOT NULL DEFAULT '',
    type                     ENUM ('SUBMITTED','STEP1_SUBMITTED') NOT NULL,

    CONSTRAINT fk_call_notification_to_user
        FOREIGN KEY (user_id) REFERENCES account(id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,

    CONSTRAINT fk_call_notification_to_project
        FOREIGN KEY (project_id) REFERENCES project(id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_call_project_notification_configuration
    MODIFY COLUMN email_subject TEXT(255) NOT NULL DEFAULT '',
    MODIFY COLUMN email_body    TEXT(10000) NOT NULL DEFAULT '';
