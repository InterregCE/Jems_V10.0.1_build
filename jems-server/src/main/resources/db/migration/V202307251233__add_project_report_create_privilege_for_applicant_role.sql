SELECT id INTO @id FROM account_role WHERE `name` = 'applicant user' ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectCreatorReportingProjectCreate');