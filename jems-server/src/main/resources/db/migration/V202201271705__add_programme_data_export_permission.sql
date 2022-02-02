INSERT IGNORE INTO account_role_permission(account_role_id, permission)
SELECT accountRole.id, 'ProgrammeDataExportRetrieve'
FROM account_role as accountRole
WHERE accountRole.name = 'administrator'
ORDER BY id DESC LIMIT 1;
