ALTER TABLE project_call_project_notification_configuration
    CHANGE COLUMN id id VARCHAR (255) NOT NULL;

ALTER TABLE notification
    CHANGE COLUMN type type VARCHAR (255) NOT NULL;
