SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'AssignmentsUnlimited');

INSERT INTO account_role_permission(account_role_id, permission)
SELECT DISTINCT id, 'AssignmentsUnlimited'
FROM account_role
    INNER JOIN account_role_permission arp on account_role.id = arp.account_role_id
WHERE arp.account_role_id != 1 AND (arp.permission LIKE 'InstitutionsAssignmentRetrieve' OR arp.permission LIKE 'InstitutionsAssignmentUpdate');
