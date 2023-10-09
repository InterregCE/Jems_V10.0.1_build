CREATE TABLE project_audit_control
(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    number INT NOT NULL,
    project_id INT UNSIGNED NOT NULL,
    project_custom_identifier VARCHAR(31) NOT NULL,
    status ENUM ('Ongoing', 'Closed') NOT NULL DEFAULT 'Ongoing',
    controlling_body ENUM (
        'Controller',
        'NationalApprobationBody',
        'RegionalApprobationBody',
        'JS',
        'MA',
        'NA',
        'GoA',
        'AA',
        'EC',
        'ECA',
        'OLAF'
        ) NOT NULL,
    control_type ENUM ('Administrative','OnTheSpot') NOT NULL,
    start_date DATETIME(3),
    end_date DATETIME(3),
    final_report_date DATETIME(3),
    total_controlled_amount DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total_corrections_amount DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    comment TEXT(2000),

    CONSTRAINT fk_audit_and_control_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);


SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;

INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectMonitorAuditAndControlView'),
       (@id, 'ProjectMonitorAuditAndControlEdit'),
       (@id, 'ProjectMonitorCloseAuditControl'),
       (@id, 'ProjectMonitorCloseAuditControlCorrection');


