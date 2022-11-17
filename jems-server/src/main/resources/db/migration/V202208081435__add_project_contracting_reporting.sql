CREATE TABLE project_contracting_reporting
(
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id    INT UNSIGNED NOT NULL,
    type          ENUM('Content', 'Finance', 'Both') NOT NULL,
    period_number SMALLINT UNSIGNED NOT NULL,
    deadline      DATE NOT NULL,
    comment       TEXT(2000) NOT NULL DEFAULT '',
    CONSTRAINT fk_project_contracting_reporting_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES  (@id, 'ProjectContractingReportingView'),
        (@id, 'ProjectContractingReportingEdit'),
        (@id, 'ProjectCreatorContractingReportingView'),
        (@id, 'ProjectCreatorContractingReportingEdit');