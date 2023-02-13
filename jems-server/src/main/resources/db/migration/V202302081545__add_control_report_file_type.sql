ALTER TABLE file_metadata
    CHANGE COLUMN type type ENUM(
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
    'PaymentAdvanceAttachment'
    ) NOT NULL;
