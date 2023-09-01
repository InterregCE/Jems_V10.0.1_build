ALTER TABLE report_project
    CHANGE COLUMN status status ENUM ('Draft', 'Submitted', 'InVerification') NOT NULL DEFAULT 'Draft',
    ADD COLUMN verification_end_date DATETIME(3) DEFAULT NULL AFTER verification_date,
    ADD COLUMN amount_requested DECIMAL(17, 2) UNSIGNED DEFAULT NULL,
    ADD COLUMN total_eligible_after_verification DECIMAL(17, 2) UNSIGNED DEFAULT NULL;

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectReportingVerificationProjectView');

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectReportingVerificationProjectEdit');

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectReportingVerificationFinalize');