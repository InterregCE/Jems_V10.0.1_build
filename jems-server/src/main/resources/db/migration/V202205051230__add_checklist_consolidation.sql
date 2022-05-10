ALTER TABLE checklist_instance
    ADD COLUMN consolidated BOOLEAN NOT NULL DEFAULT FALSE;

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectAssessmentChecklistConsolidate');
