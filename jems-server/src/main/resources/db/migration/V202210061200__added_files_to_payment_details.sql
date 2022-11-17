ALTER TABLE report_project_file
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
    'Contract',
    'ContractDoc',
    'ContractPartnerDoc',
    'ContractInternal',
    'PaymentAttachment'
    ) NOT NULL;
