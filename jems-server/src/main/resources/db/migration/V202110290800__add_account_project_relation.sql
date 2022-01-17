CREATE TABLE account_project
(
    account_id INT UNSIGNED NOT NULL,
    project_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (account_id, project_id),
    CONSTRAINT fk_account_id_to_account
        FOREIGN KEY (account_id)
            REFERENCES account (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_id_to_project
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectRetrieveEditUserAssignments');
