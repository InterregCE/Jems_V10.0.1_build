ALTER TABLE project_partner_budget_staff_cost
    DROP COLUMN unit_type;

ALTER TABLE project_partner_budget_staff_cost_transl
    ADD COLUMN unit_type TEXT(100) DEFAULT NULL
