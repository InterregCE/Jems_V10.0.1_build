ALTER TABLE project_contracting_dimension_code
    CHANGE COLUMN programme_objective_dimension programme_objective_dimension ENUM (
    'TypesOfIntervention',
    'FormOfSupport',
    'TerritorialDeliveryMechanism',
    'EconomicActivity',
    'Location',
    'GenderEquality',
    'RegionalAndSeaBasinStrategy') NOT NULL,
    CHANGE COLUMN dimension_code dimension_code VARCHAR(255) NOT NULL;


