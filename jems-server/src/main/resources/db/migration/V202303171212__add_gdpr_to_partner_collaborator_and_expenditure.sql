ALTER TABLE account_partner_collaborator
    ADD COLUMN gdpr BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE report_project_partner_expenditure
    ADD COLUMN gdpr BOOLEAN NOT NULL DEFAULT FALSE;
