CREATE TABLE account_profile
(
    account_id      INTEGER PRIMARY KEY,
    language        VARCHAR(50),
    CONSTRAINT fk_account_profile_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE CASCADE
);
