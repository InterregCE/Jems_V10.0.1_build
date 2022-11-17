ALTER TABLE report_project_partner_identification
    ADD COLUMN format_originals  BOOLEAN NOT NULL DEFAULT FALSE AFTER next_report_forecast,
    ADD COLUMN format_copy       BOOLEAN NOT NULL DEFAULT FALSE AFTER format_originals,
    ADD COLUMN format_electronic BOOLEAN NOT NULL DEFAULT FALSE AFTER format_copy,
    ADD COLUMN type              ENUM('PartnerReport', 'FinalReport') NOT NULL DEFAULT 'PartnerReport' AFTER format_electronic;
