CREATE TABLE mail_confirmation
(
    token      BINARY(16) PRIMARY KEY NOT NULL, # UUID
    account_id INT UNSIGNED,
    timestamp  DATETIME(3) NOT NULL DEFAULT current_timestamp(3),
    clicked    BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_mail_confirmation_to_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
