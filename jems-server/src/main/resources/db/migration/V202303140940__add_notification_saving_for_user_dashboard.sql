CREATE TABLE notification
(
    id                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    account_id         INT UNSIGNED                                      NOT NULL,
    project_id         INT UNSIGNED                                               DEFAULT NULL,
    project_identifier VARCHAR(31)                                                DEFAULT NULL,
    project_acronym    VARCHAR(25)                                                DEFAULT NULL,
    created            DATETIME(3)                                       NOT NULL,
    subject            VARCHAR(255)                                      NOT NULL DEFAULT '',
    body               TEXT(10000)                                       NOT NULL DEFAULT '',
    type               ENUM ('ProjectSubmittedStep1','ProjectSubmitted') NOT NULL,

    CONSTRAINT fk_call_notification_to_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,

    CONSTRAINT fk_call_notification_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

DELETE FROM project_call_project_notification_configuration;

ALTER TABLE project_call_project_notification_configuration
    MODIFY COLUMN email_subject VARCHAR(255) NOT NULL DEFAULT '',
    MODIFY COLUMN email_body TEXT(10000) NOT NULL DEFAULT '',
    CHANGE COLUMN id id ENUM ('ProjectSubmittedStep1','ProjectSubmitted') NOT NULL;
