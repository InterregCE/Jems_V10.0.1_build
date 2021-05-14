ALTER TABLE project_partner_budget_staff_cost_transl
    DROP SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_staff_cost_transl
    CHANGE COLUMN `budget_id`
        source_entity_id INT UNSIGNED NOT NULL;

ALTER TABLE project_partner_budget_staff_cost_transl
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_equipment_transl
    CHANGE COLUMN `budget_id`
        source_entity_id INT UNSIGNED NOT NULL;

ALTER TABLE project_partner_budget_external_transl
    CHANGE COLUMN `budget_id`
        source_entity_id INT UNSIGNED NOT NULL;

ALTER TABLE project_partner_budget_travel_transl
    CHANGE COLUMN `budget_id`
        source_entity_id INT UNSIGNED NOT NULL;

ALTER TABLE project_partner_budget_infrastructure_transl
    CHANGE COLUMN `budget_id`
        source_entity_id INT UNSIGNED NOT NULL;


ALTER TABLE project_partner_budget_travel_transl
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_travel_period
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_travel
    ADD SYSTEM VERSIONING;


ALTER TABLE project_partner_budget_infrastructure_transl
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_infrastructure_period
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_infrastructure
    ADD SYSTEM VERSIONING;


ALTER TABLE project_partner_budget_external_transl
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_external_period
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_external
    ADD SYSTEM VERSIONING;


ALTER TABLE project_partner_budget_equipment_transl
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_equipment_period
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_equipment
    ADD SYSTEM VERSIONING;


ALTER TABLE project_partner_budget_unit_cost
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_budget_unit_cost_period
    ADD SYSTEM VERSIONING;

ALTER TABLE project_partner_lump_sum
    ADD SYSTEM VERSIONING;
