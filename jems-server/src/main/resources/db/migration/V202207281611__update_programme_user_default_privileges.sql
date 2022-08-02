SELECT id INTO @id FROM account_role WHERE `name` = 'programme user' ORDER BY id DESC LIMIT 1;
DELETE FROM account_role_permission WHERE account_role_id = @id AND permission IN (
    'ProjectReportingEdit',
    'ProjectSetToContracted',
    'ProjectFileApplicationUpdate',
    'ProjectStatusDecisionRevert'
);

SELECT id INTO @id FROM account_role WHERE `name` = 'programme user' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectContractingManagementView'),
       (@id, 'ProjectAssessmentChecklistSelectedRetrieve'),
       (@id, 'ProgrammeDataExportRetrieve'),
       (@id, 'InstitutionsRetrieve');


