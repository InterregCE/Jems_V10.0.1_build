ALTER TABLE report_project_partner
    ADD COLUMN last_control_reopening DATETIME(3) DEFAULT NULL AFTER last_re_submission,
    CHANGE COLUMN status status ENUM (
        'Draft',
        'Submitted',
        'ReOpenSubmittedLast',
        'ReOpenSubmittedLimited',
        'InControl',
        'ReOpenInControlLast',
        'ReOpenInControlLimited',
        'Certified',
        'ReOpenCertified'
    ) DEFAULT 'Draft';


ALTER TABLE report_project_partner_expenditure_parked
    ADD COLUMN parked_on DATETIME(3);


SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectPartnerControlReportingReOpen');

-- remove old notification types
DELETE FROM notification
WHERE type IN  ('PartnerReportReOpenFromSubmitted', 'PartnerReportReOpenFromControlOngoing');

DELETE FROM project_call_project_notification_configuration
WHERE id IN ('PartnerReportReOpenFromSubmitted', 'PartnerReportReOpenFromControlOngoing')