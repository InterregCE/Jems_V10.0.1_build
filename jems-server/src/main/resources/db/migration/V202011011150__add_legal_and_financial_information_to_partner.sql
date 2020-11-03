ALTER TABLE project_partner
    ADD COLUMN partner_type    VARCHAR(127) DEFAULT NULL AFTER department,
    ADD COLUMN legal_status_id INT UNSIGNED NOT NULL AFTER partner_type,
    ADD COLUMN vat             VARCHAR(50)  DEFAULT NULL AFTER legal_status_id,
    ADD COLUMN vat_recovery    BOOLEAN      DEFAULT TRUE AFTER vat,
    ADD CONSTRAINT fk_project_partner_to_programme_legal_status
        FOREIGN KEY (legal_status_id)
            REFERENCES programme_legal_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;