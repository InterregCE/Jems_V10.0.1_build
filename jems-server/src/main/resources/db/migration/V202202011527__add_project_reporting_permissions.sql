INSERT IGNORE INTO account_role_permission(account_role_id, permission)
SELECT accountRole.id, 'ProjectReportingView'
FROM account_role as accountRole
WHERE accountRole.name = 'administrator'
ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
SELECT accountRole.id, 'ProjectReportingEdit'
FROM account_role as accountRole
WHERE accountRole.name = 'administrator'
ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
SELECT accountRole.id, 'ProjectReportingView'
FROM account_role as accountRole
WHERE accountRole.name = 'programme user'
ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
SELECT accountRole.id, 'ProjectReportingEdit'
FROM account_role as accountRole
WHERE accountRole.name = 'programme user'
ORDER BY id DESC LIMIT 1;
