SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;

INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectCreatorSharedFolderView'),
       (@id, 'ProjectCreatorSharedFolderEdit'),
       (@id, 'ProjectMonitorSharedFolderView'),
       (@id, 'ProjectMonitorSharedFolderEdit');

ALTER TABLE file_metadata
    CHANGE COLUMN type type ENUM (
    'PartnerReport',
    'WorkPackage',
    'Activity',
    'Deliverable',
    'Output',
    'Expenditure',
    'ProcurementAttachment',
    'Contribution',
    'ControlDocument',
    'ControlCertificate',
    'ControlReport',
    'Contract',
    'ContractDoc',
    'ContractPartnerDoc',
    'ContractInternal',
    'PaymentAttachment',
    'PaymentAdvanceAttachment',
    'ProjectReport',
    'ProjectResult',
    'ActivityProjectReport',
    'DeliverableProjectReport',
    'OutputProjectReport',
    'SharedFolder'
    ) NOT NULL;
