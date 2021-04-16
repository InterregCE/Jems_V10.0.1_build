CREATE TABLE account_role_permission
(
    account_role_id INT UNSIGNED NOT NULL,
    permission      VARCHAR(255) NOT NULL,
    PRIMARY KEY (account_role_id, permission),
    CONSTRAINT fk_account_role_permission_to_account_role
        FOREIGN KEY (account_role_id) REFERENCES account_role (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectSubmission'),
       (@id, 'RoleRetrieve'),
       (@id, 'RoleCreate'),
       (@id, 'RoleUpdate'),
       (@id, 'UserRetrieve'),
       (@id, 'UserCreate'),
       (@id, 'UserUpdate'),
       (@id, 'UserUpdateRole'),
       (@id, 'UserUpdatePassword');
