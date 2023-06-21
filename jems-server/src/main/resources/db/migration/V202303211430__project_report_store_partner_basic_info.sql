ALTER TABLE report_project_spending_profile
    ADD COLUMN partner_number       INT                              NOT NULL AFTER partner_id,
    ADD COLUMN partner_abbreviation VARCHAR(15)                      NOT NULL AFTER partner_number,
    ADD COLUMN partner_role         ENUM ('PARTNER', 'LEAD_PARTNER') NOT NULL AFTER partner_abbreviation,
    ADD COLUMN country              VARCHAR(100) DEFAULT NULL                 AFTER partner_role;
