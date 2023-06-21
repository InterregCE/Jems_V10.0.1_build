CREATE TABLE project_call_project_notification_configuration
(
    id                       ENUM ('SUBMITTED','STEP1_SUBMITTED') NOT NULL,
    call_id                  INT UNSIGNED NOT NULL,
    active                   BOOLEAN NOT NULL DEFAULT FALSE,
    send_to_manager          BOOLEAN NOT NULL DEFAULT FALSE,
    send_to_lead_partner     BOOLEAN NOT NULL DEFAULT FALSE,
    send_to_project_partners BOOLEAN NOT NULL DEFAULT FALSE,
    send_to_project_assigned BOOLEAN NOT NULL DEFAULT FALSE,
    email_subject            TEXT(255) DEFAULT NULL,
    email_body               TEXT(10000) DEFAULT NULL,
    PRIMARY KEY (id, call_id),
    CONSTRAINT fk_project_notification_to_call
        FOREIGN KEY (call_id) REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
