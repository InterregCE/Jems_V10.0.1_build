ALTER TABLE project_call_project_notification_configuration
    ADD COLUMN send_to_controllers BOOLEAN NOT NULL DEFAULT FALSE AFTER send_to_project_assigned;

ALTER TABLE notification
    ADD COLUMN partner_id           INT UNSIGNED                     DEFAULT NULL AFTER project_acronym,
    ADD COLUMN partner_role         ENUM ('PARTNER', 'LEAD_PARTNER') DEFAULT NULL AFTER partner_id,
    ADD COLUMN partner_number       INT UNSIGNED                     DEFAULT NULL AFTER partner_role;
