ALTER TABLE audit_control
CHANGE COLUMN controlling_body controlling_body ENUM (
    'Controller',
    'NationalApprobationBody',
    'RegionalApprobationBody',
    'JS',
    'MA',
    'MABAF',
    'NA',
    'GoA',
    'AA',
    'EC',
    'ECA',
    'OLAF'
) NOT NULL;
