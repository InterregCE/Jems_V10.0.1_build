ALTER TABLE checklist_instance
    ADD COLUMN visible BOOLEAN NOT NULL DEFAULT FALSE;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
    SELECT accountRole.id, 'ProjectAssessmentChecklistSelectedRetrieve'
    FROM account_role as accountRole WHERE accountRole.name = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
    SELECT accountRole.id, 'ProjectAssessmentChecklistSelectedUpdate'
    FROM account_role as accountRole WHERE accountRole.name = 'administrator' ORDER BY id DESC LIMIT 1;

