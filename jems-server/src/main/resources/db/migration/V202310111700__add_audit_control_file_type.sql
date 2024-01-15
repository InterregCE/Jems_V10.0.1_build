
ALTER TABLE file_metadata
    CHANGE COLUMN type type ENUM (
    'PartnerReport',
    'WorkPackage',
    'Activity',
    'Deliverable',
    'Output',
    'Expenditure',
    'ProcurementAttachment',
    'ProcurementGdprAttachment',
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
    'PaymentToEcAttachment',
    'ProjectReport',
    'ProjectResult',
    'ActivityProjectReport',
    'DeliverableProjectReport',
    'OutputProjectReport',
    'VerificationDocument',
    'VerificationCertificate',
    'SharedFolder',
    'CallTranslation',
    'CallTranslationArchive',
    'AuditControl'
    ) NOT NULL;
