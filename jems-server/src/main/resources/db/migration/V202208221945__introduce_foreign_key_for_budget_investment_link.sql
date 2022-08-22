UPDATE project_partner_budget_infrastructure
SET investment_id = NULL
WHERE investment_id NOT IN (
    SELECT id FROM project_work_package_investment
);
ALTER TABLE project_partner_budget_infrastructure
    ADD CONSTRAINT fk_project_partner_budget_infra_to_wp_investment FOREIGN KEY (investment_id)
        REFERENCES project_work_package_investment (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT;


UPDATE project_partner_budget_equipment
SET investment_id = NULL
WHERE investment_id NOT IN (
    SELECT id FROM project_work_package_investment
);
ALTER TABLE project_partner_budget_equipment
    ADD CONSTRAINT fk_project_partner_budget_equipment_to_wp_investment FOREIGN KEY (investment_id)
        REFERENCES project_work_package_investment (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT;


UPDATE project_partner_budget_external
SET investment_id = NULL
WHERE investment_id NOT IN (
    SELECT id FROM project_work_package_investment
);
ALTER TABLE project_partner_budget_external
    ADD CONSTRAINT fk_project_partner_budget_external_to_wp_investment FOREIGN KEY (investment_id)
        REFERENCES project_work_package_investment (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT;
