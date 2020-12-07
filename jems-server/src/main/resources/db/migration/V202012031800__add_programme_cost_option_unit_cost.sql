CREATE TABLE programme_unit_cost
(
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    name          VARCHAR(50)    NOT NULL,
    description   VARCHAR(500) DEFAULT NULL,
    type          VARCHAR(25)    NOT NULL,
    cost_per_unit DECIMAL(11, 2) NOT NULL
);

CREATE TABLE programme_unit_cost_budget_category
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    programme_unit_cost_id INT UNSIGNED NOT NULL,
    category               ENUM (
        'StaffCosts',
        'OfficeAndAdministrationCosts',
        'TravelAndAccommodationCosts',
        'ExternalCosts',
        'EquipmentCosts',
        'InfrastructureCosts' )         NOT NULL,
    CONSTRAINT fk_programme_unit_cost_budget_category_to_programme_unit_cost
        FOREIGN KEY (programme_unit_cost_id) REFERENCES programme_unit_cost (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
