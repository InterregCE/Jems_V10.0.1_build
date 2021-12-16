CREATE TABLE account_project_collaborator
(
    account_id INT UNSIGNED NOT NULL,
    project_id INT UNSIGNED NOT NULL,
    level      ENUM('VIEW', 'EDIT', 'MANAGE') NOT NULL DEFAULT 'VIEW',
    PRIMARY KEY (account_id, project_id),
    CONSTRAINT fk_account_id_to_account_2
        FOREIGN KEY (account_id)
            REFERENCES account (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_id_to_project_2
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

-- assign MANAGE to all already existing project owners
INSERT INTO account_project_collaborator (account_id, project_id, level)
SELECT applicant_id, id, 'MANAGE'
FROM project;

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectCreatorCollaboratorsRetrieve'),
       (@id, 'ProjectCreatorCollaboratorsUpdate'),
       (@id, 'ProjectMonitorCollaboratorsRetrieve'),
       (@id, 'ProjectMonitorCollaboratorsUpdate');

SELECT id INTO @id FROM account_role WHERE `name` = 'programme user' ORDER BY id DESC LIMIT 1;
INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectMonitorCollaboratorsRetrieve'),
       (@id, 'ProjectMonitorCollaboratorsUpdate');

SELECT id INTO @id FROM account_role WHERE `name` = 'applicant user' ORDER BY id DESC LIMIT 1;
INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectCreatorCollaboratorsRetrieve'),
       (@id, 'ProjectCreatorCollaboratorsUpdate');
