ALTER TABLE project_partner_budget_staff_cost
    ADD COLUMN
        unit_type ENUM ('PERIOD','MONTH','HOUR') DEFAULT NULL;
ALTER TABLE project_partner_budget_staff_cost
    ADD COLUMN
        type ENUM ('REAL_COST','UNIT_COST') DEFAULT NULL;
ALTER TABLE project_partner_budget_staff_cost_transl
    ADD COLUMN
        comment TEXT(255) DEFAULT NULL;


ALTER TABLE project_partner_budget_travel_transl
    ADD COLUMN
        unit_type TEXT(100) DEFAULT NULL;


ALTER TABLE project_partner_budget_infrastructure_transl
    ADD COLUMN
        unit_type TEXT(100) DEFAULT NULL;
ALTER TABLE project_partner_budget_infrastructure_transl
    ADD COLUMN
        award_procedures TEXT(250) DEFAULT NULL;
ALTER TABLE project_partner_budget_infrastructure
    ADD COLUMN
        investment_id  INT UNSIGNED DEFAULT NULL;


ALTER TABLE project_partner_budget_external_transl
    ADD COLUMN
        unit_type TEXT(100) DEFAULT NULL;
ALTER TABLE project_partner_budget_external_transl
    ADD COLUMN
        award_procedures TEXT(250) DEFAULT NULL;
ALTER TABLE project_partner_budget_external
    ADD COLUMN
        investment_id INT UNSIGNED DEFAULT NULL;


ALTER TABLE project_partner_budget_equipment_transl
    ADD COLUMN
        unit_type TEXT(100) DEFAULT NULL;
ALTER TABLE project_partner_budget_equipment_transl
    ADD COLUMN
        award_procedures TEXT(250) DEFAULT NULL;
ALTER TABLE project_partner_budget_equipment
    ADD COLUMN
        investment_id INT UNSIGNED DEFAULT NULL;


