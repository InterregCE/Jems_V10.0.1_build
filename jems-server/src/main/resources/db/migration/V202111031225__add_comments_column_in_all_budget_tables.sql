SET @@system_versioning_alter_history = 1;
ALTER TABLE project_partner_budget_staff_cost_transl
    CHANGE COLUMN comment comments
    TEXT(255) DEFAULT NULL;

ALTER TABLE project_partner_budget_travel_transl
    ADD COLUMN comments TEXT(255) DEFAULT NULL;

ALTER TABLE project_partner_budget_external_transl
    ADD COLUMN comments TEXT(255) DEFAULT NULL;

ALTER TABLE project_partner_budget_equipment_transl
    ADD COLUMN comments TEXT(255) DEFAULT NULL;

ALTER TABLE project_partner_budget_infrastructure_transl
    ADD COLUMN comments TEXT(255) DEFAULT NULL;
