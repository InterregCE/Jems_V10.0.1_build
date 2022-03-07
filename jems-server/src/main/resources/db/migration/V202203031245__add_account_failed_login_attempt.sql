CREATE TABLE account_failed_login_attempt
(
    email           VARCHAR(255) PRIMARY KEY,
    count           SMALLINT UNSIGNED NOT NULL,
    last_attempt_at DATETIME(3) NOT NULL,
    CONSTRAINT fk_account_failed_login_attempt_to_account
        FOREIGN KEY (email) REFERENCES account (email)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);
