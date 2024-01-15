ALTER TABLE payment_partner_installment
    ADD COLUMN project_correction_id INT UNSIGNED DEFAULT NULL,
    ADD CONSTRAINT fk_payment_installment_to_audit_control_correction
    FOREIGN KEY(project_correction_id) REFERENCES audit_control_correction(id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT;

ALTER TABLE audit_control_correction
    ADD COLUMN project_modification_id INT UNSIGNED DEFAULT NULL,
    ADD CONSTRAINT fk_audit_control_correction_to_project_status
    FOREIGN KEY(project_modification_id) REFERENCES project_status(id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT;
