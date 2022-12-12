CREATE TABLE report_project
(
    id                                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id                             INT UNSIGNED NOT NULL,
    number                                 INT          NOT NULL,
    status                                 ENUM ('Draft', 'Submitted', 'Verified', 'Paid') NOT NULL DEFAULT 'Draft',
    application_form_version               VARCHAR(127) NOT NULL,
    start_date                             DATE DEFAULT NULL,
    end_date                               DATE DEFAULT NULL,

    deadline_id                            INT UNSIGNED DEFAULT NULL,
    type                                   ENUM ('Content', 'Finance', 'Both') DEFAULT NULL,
    reporting_date                         DATE DEFAULT NULL,
    period_number                          SMALLINT UNSIGNED DEFAULT NULL,
    project_identifier                     VARCHAR(31)  NOT NULL,
    project_acronym                        VARCHAR(25)  NOT NULL,
    lead_partner_name_in_original_language VARCHAR(127) NOT NULL DEFAULT '',
    lead_partner_name_in_english           VARCHAR(127) NOT NULL DEFAULT '',

    created_at                             DATETIME(3)  NOT NULL DEFAULT current_timestamp (3),
    first_submission                       DATETIME(3)           DEFAULT NULL,
    verification_date                      DATETIME(3)           DEFAULT NULL,

    CONSTRAINT fk_report_project_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_project_to_project_contracting_reporting
        FOREIGN KEY (deadline_id) REFERENCES project_contracting_reporting (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
    UNIQUE KEY project_report_number (project_id, number)
);

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT IGNORE INTO account_role_permission(account_role_id, permission)
VALUES  (@id, 'ProjectReportingProjectView'),
        (@id, 'ProjectReportingProjectEdit');
