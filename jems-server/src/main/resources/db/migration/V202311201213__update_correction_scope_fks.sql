ALTER TABLE audit_control_correction
     DROP CONSTRAINT fk_correction_expenditure_to_project_partner_expenditure,
     DROP CONSTRAINT fk_correction_procurement_to_procurement;



ALTER TABLE audit_control_correction

    ADD CONSTRAINT fk_correction_expenditure_to_project_partner_expenditure
        FOREIGN KEY (expenditure_id) REFERENCES report_project_partner_expenditure (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,

    ADD CONSTRAINT fk_correction_procurement_to_procurement
        FOREIGN KEY (procurement_id) REFERENCES report_project_partner_procurement (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;
