CREATE TABLE report_project_partner_procurement_gdpr_file
(
    id                   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    procurement_id       INT UNSIGNED NOT NULL,
    created_in_report_id INT UNSIGNED NOT NULL,
    file_id              INT UNSIGNED NOT NULL,
    CONSTRAINT fk_procurement_gdpr_file_to_procurement
        FOREIGN KEY (procurement_id) REFERENCES report_project_partner_procurement (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_procurement_gdpr_file_to_report_partner
        FOREIGN KEY (created_in_report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_gdpr_procurement_to_file_metadata
        FOREIGN KEY(file_id) REFERENCES file_metadata(id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

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
    'ProjectReport',
    'ProjectResult',
    'ActivityProjectReport',
    'DeliverableProjectReport',
    'OutputProjectReport',
    'SharedFolder',
    'CallTranslation',
    'CallTranslationArchive'
    ) NOT NULL;


