RENAME TABLE project_partner_budget_office_administration TO project_partner_budget_options;

ALTER TABLE project_partner_budget_options
    CHANGE COLUMN `flat_rate`
        office_administration_flat_rate
            INT DEFAULT 15;

ALTER TABLE project_partner_budget_options
    ADD staff_costs_flat_rate INT DEFAULT 20;

ALTER TABLE project_partner_budget_options
    DROP CONSTRAINT fk_project_partner_budget_office_administrati_to_project_partner;

ALTER TABLE project_partner_budget_options
    ADD CONSTRAINT
        fk_project_partner_budget_options_to_project_partner
        FOREIGN KEY (partner_id)
            REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;


