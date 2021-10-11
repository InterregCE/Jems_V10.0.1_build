CREATE TABLE account_confirmation
(
    token     BINARY(16) PRIMARY KEY NOT NULL, # UUID
    user_id   INT UNSIGNED,
    timestamp DATETIME(3)            NOT NULL DEFAULT current_timestamp(3),
    confirmed BOOLEAN                NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_account_confirmation_to_account
        FOREIGN KEY (user_id) REFERENCES account (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
