CREATE TABLE password_reset_token
(
    email        VARCHAR(255) PRIMARY KEY,
    token        BINARY(16) NOT NULL,
    generated_at DATETIME(3) NOT NULL,

    CONSTRAINT fk_account_reset_password_token_to_account
        FOREIGN KEY (email) REFERENCES account (email)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
