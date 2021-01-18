DELETE FROM project_partner_budget_unit_cost WHERE programme_unit_cost_id IS NULL;
ALTER TABLE project_partner_budget_unit_cost MODIFY COLUMN programme_unit_cost_id INT UNSIGNED NOT NULL;
