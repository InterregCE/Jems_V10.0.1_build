ALTER TABLE report_project_partner
    ADD COLUMN last_re_submission DATETIME(3) DEFAULT NULL AFTER first_submission,
    CHANGE COLUMN status status ENUM (
        'Draft',
        'Submitted',
        'ReOpenSubmittedLast',
        'ReOpenSubmittedLimited',
        'InControl',
        'ReOpenInControlLast',
        'ReOpenInControlLimited',
        'Certified'
    ) DEFAULT 'Draft';


SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectReportingReOpen');
