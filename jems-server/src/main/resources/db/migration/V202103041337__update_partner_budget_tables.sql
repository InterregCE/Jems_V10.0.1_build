-- add single unit cost option to partner_budget tables

ALTER TABLE project_partner_budget_equipment
    ADD COLUMN
        unit_cost_id INT UNSIGNED DEFAULT NULL;

ALTER TABLE project_partner_budget_external
    ADD COLUMN
        unit_cost_id INT UNSIGNED DEFAULT NULL;

ALTER TABLE project_partner_budget_infrastructure
    ADD COLUMN
        unit_cost_id  INT UNSIGNED DEFAULT NULL;

ALTER TABLE project_partner_budget_staff_cost
    ADD COLUMN
        unit_cost_id  INT UNSIGNED DEFAULT NULL;

ALTER TABLE project_partner_budget_travel
    ADD COLUMN
        unit_cost_id  INT UNSIGNED DEFAULT NULL;





