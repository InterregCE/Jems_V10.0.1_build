ALTER TABLE audit_control_correction
    ADD COLUMN expenditure_id INT UNSIGNED after programme_fund_id,
    ADD COLUMN cost_category  ENUM (
        'Staff',
        'Office',
        'Travel',
        'External',
        'Equipment',
        'Infrastructure',
        'Other',
        'LumpSum',
        'UnitCost',
        'SpfCost') DEFAULT null after expenditure_id,
    ADD COLUMN procurement_id INT UNSIGNED after cost_category,

    ADD CONSTRAINT fk_correction_expenditure_to_project_partner_expenditure
        FOREIGN KEY (expenditure_id) REFERENCES report_project_partner_expenditure (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,

    ADD CONSTRAINT fk_correction_procurement_to_procurement
        FOREIGN KEY (procurement_id) REFERENCES report_project_partner_procurement (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;
