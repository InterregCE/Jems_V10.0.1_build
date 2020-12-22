ALTER TABLE project_partner_budget_options
    ADD COLUMN
        other_costs_on_staff_costs_flat_rate TINYINT UNSIGNED DEFAULT NULL;

ALTER TABLE project_partner_budget_options
    CHANGE COLUMN `office_administration_flat_rate` office_and_administration_on_staff_costs_flat_rate TINYINT UNSIGNED DEFAULT NULL;

ALTER TABLE project_partner_budget_options
    CHANGE COLUMN `travel_accommodation_flat_rate` travel_and_accommodation_on_staff_costs_flat_rate TINYINT UNSIGNED DEFAULT NULL;
