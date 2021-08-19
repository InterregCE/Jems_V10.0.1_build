ALTER TABLE project_call
    ADD COLUMN allow_real_staff_costs BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN allow_real_travel_and_accommodation_costs BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN allow_real_external_expertise_and_services_costs BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN allow_real_equipment_costs BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN allow_real_infrastructure_costs BOOLEAN NOT NULL DEFAULT TRUE;
