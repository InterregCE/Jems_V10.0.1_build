CREATE TABLE report_project_partner_procurement_file
(
    id                   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    procurement_id       INT UNSIGNED NOT NULL,
    created_in_report_id INT UNSIGNED NOT NULL,
    file_id              INT UNSIGNED NOT NULL,
    CONSTRAINT fk_procurement_file_to_procurement
        FOREIGN KEY (procurement_id) REFERENCES report_project_partner_procurement (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_procurement_file_to_report_partner
        FOREIGN KEY (created_in_report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_procurement_file_to_file
        FOREIGN KEY (file_id) REFERENCES report_project_file (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

DELETE FROM report_project_file WHERE type = 'Procurement';

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
    'Contract',
    'ContractDoc',
    'ContractPartnerDoc',
    'ContractInternal'
    ) NOT NULL,
    ADD COLUMN description TEXT(5000) NOT NULL DEFAULT '';
