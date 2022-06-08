ALTER TABLE report_project_file
    CHANGE COLUMN type type ENUM(
    'PartnerReport',
    'WorkPackage',
    'Activity',
    'Deliverable',
    'Output',
    'Expenditure',
    'Procurement',
    'Contribution'
) NOT NULL,
    CHANGE COLUMN path path VARCHAR(255) NOT NULL;
