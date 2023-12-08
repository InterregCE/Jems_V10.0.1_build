CREATE TABLE payment_application_to_ec_audit_export
(
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    plugin_key        VARCHAR(255) NOT NULL,
    generated_file_id INT UNSIGNED NOT NULL,
    accounting_year              SMALLINT DEFAULT NULL,
    fund_type ENUM (
        'ERDF',
        'IPA III CBC',
        'Neighbourhood CBC',
        'IPA III',
        'NDICI',
        'OCTP',
        'Interreg Funds',
        'Other') DEFAULT NULL,
    request_time      DATETIME(3)  NOT NULL,
    export_started_at DATETIME(3),
    export_ended_at   DATETIME(3),

    CONSTRAINT fk_payment_application_to_ec_audit_to_file_metadata
        FOREIGN KEY(generated_file_id) REFERENCES file_metadata(id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;

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
    'PaymentToEcAuditExport',
    'PaymentAuditAttachment'
    ) NOT NULL;
