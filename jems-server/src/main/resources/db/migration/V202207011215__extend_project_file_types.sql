ALTER TABLE report_project_file
    CHANGE COLUMN type type ENUM(
    'PartnerReport',
    'WorkPackage',
    'Activity',
    'Deliverable',
    'Output',
    'Expenditure',
    'Procurement',
    'Contribution',
    'Contract',
    'ContractDoc',
    'ContractPartnerDoc',
    'ContractInternal'
    ) NOT NULL;
