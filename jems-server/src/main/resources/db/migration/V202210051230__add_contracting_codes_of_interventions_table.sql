CREATE TABLE project_contracting_dimension_code(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id INT UNSIGNED NOT NULL,
    programme_objective_dimension ENUM (
        'TypesOfIntervention',
        'FormOfSupport',
        'TerritorialDeliveryMechanism',
        'EconomicActivity',
        'GenderEquality',
        'RegionalAndSeaBasinStrategy') NOT NULL,
    dimension_code VARCHAR(10) NOT NULL,
    project_budget_amount_share DECIMAL(11, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    UNIQUE(project_id, programme_objective_dimension, dimension_code),
    CONSTRAINT fk_project_contracting_codes_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
