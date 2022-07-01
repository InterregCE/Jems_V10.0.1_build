CREATE TABLE programme_objective_dimension_code
(
    programme_objective_dimension ENUM ('TypesOfIntervention',
                                        'FormOfSupport',
                                        'TerritorialDeliveryMechanism',
                                        'EconomicActivity',
                                        'GenderEquality',
                                        'RegionalAndSeaBasinStrategy') NOT NULL,
    code                          VARCHAR(10)                          NOT NULL,
    objective_code                VARCHAR(127)                         NOT NULL,
    PRIMARY KEY (programme_objective_dimension, code, objective_code),
    CONSTRAINT FK_PRGROBJDIMEC_ON_SPECOBJPROGR FOREIGN KEY (objective_code)
        REFERENCES programme_priority_specific_objective (programme_objective_policy_code)
        ON DELETE CASCADE
);
