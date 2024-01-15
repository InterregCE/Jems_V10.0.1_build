CREATE TABLE payment_audit_export_metadata
(
    id                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    plugin_key         VARCHAR(255) NOT NULL,
    accounting_year_id INT UNSIGNED DEFAULT NULL,
    programme_fund_id  INT UNSIGNED DEFAULT NULL,
    file_name          VARCHAR(255),
    content_type       VARCHAR(255),
    request_time       DATETIME(3) NOT NULL,
    export_started_at  DATETIME(3),
    export_ended_at    DATETIME(3),

    CONSTRAINT fk_audit_export_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT,
    CONSTRAINT fk_audit_export_to_accounting_year FOREIGN KEY (accounting_year_id) REFERENCES accounting_years (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
);

SELECT id
INTO @id
FROM account_role
WHERE `name` = 'administrator'
ORDER BY id DESC LIMIT 1;

INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'PaymentsAuditRetrieve'),
       (@id, 'PaymentsAuditUpdate');

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
    'AuditControl',
    'PaymentAuditExport',
    'PaymentAuditAttachment'
    ) NOT NULL;
