ALTER TABLE project_contracting_partner_state_aid_gber
    ADD COLUMN amount_granting_aid DECIMAL(17, 2) UNSIGNED DEFAULT NULL;

ALTER TABLE project_contracting_partner_state_aid_minimis
    ADD COLUMN amount_granting_aid DECIMAL(17, 2) UNSIGNED DEFAULT NULL,
    MODIFY COLUMN aid_granted_by_country VARCHAR(250) DEFAULT NULL;

ALTER TABLE project_contracting_partner_state_aid_minimis
    DROP COLUMN aid_granted_by_country_code;



