ALTER TABLE project_partner_state_aid
    DROP SYSTEM VERSIONING;
ALTER TABLE project_partner_state_aid
    ADD COLUMN state_aid_id INT UNSIGNED NULL;
ALTER TABLE project_partner_state_aid
    ADD CONSTRAINT fk_project_partner_state_aid_to_call_state_aid
        FOREIGN KEY (state_aid_id) REFERENCES project_call_state_aid (programme_state_aid)
            ON DELETE CASCADE;
ALTER TABLE project_partner_state_aid
    ADD SYSTEM VERSIONING;
