CREATE TABLE mail_notification
(
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    subject      VARCHAR(800)   NOT NULL,
    body         VARCHAR(15000) NOT NULL,
    message_type VARCHAR(255)   NOT NULL
);
CREATE TABLE mail_notification_recipient
(
    notification_id INT UNSIGNED,
    recipient       VARCHAR(255) NOT NULL,
    PRIMARY KEY (notification_id, recipient),
    CONSTRAINT fk_recipient_to_mail_notification
        FOREIGN KEY (notification_id) REFERENCES mail_notification (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
