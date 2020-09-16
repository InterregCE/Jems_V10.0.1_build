CREATE TABLE project_description_c7_management
(
    project_id                               INTEGER PRIMARY KEY,
    project_coordination                     TEXT                                                   DEFAULT NULL,
    project_quality_assurance                TEXT                                                   DEFAULT NULL,
    project_communication                    TEXT                                                   DEFAULT NULL,
    project_financial_management             TEXT                                                   DEFAULT NULL,
    # C7.5 cooperation criteria:
    project_joint_development                BOOLEAN                                                DEFAULT FALSE,
    project_joint_development_description    TEXT                                                   DEFAULT NULL,
    project_joint_implementation             BOOLEAN                                                DEFAULT FALSE,
    project_joint_implementation_description TEXT                                                   DEFAULT NULL,
    project_joint_staffing                   BOOLEAN                                                DEFAULT FALSE,
    project_joint_staffing_description       TEXT                                                   DEFAULT NULL,
    project_joint_financing                  BOOLEAN                                                DEFAULT FALSE,
    project_joint_financing_description      TEXT                                                   DEFAULT NULL,
    # C7.6 horizontal principles:
    sustainable_development_criteria_effect  ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,
    sustainable_development_description      TEXT                                                   DEFAULT NULL,
    equal_opportunities_effect               ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,
    equal_opportunities_description          TEXT                                                   DEFAULT NULL,
    sexual_equality_effect                   ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,
    sexual_equality_description              TEXT                                                   DEFAULT NULL,
    CONSTRAINT fk_project_management_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_description_c8_long_term_plans
(
    project_id              INTEGER PRIMARY KEY,
    project_ownership       TEXT DEFAULT NULL,
    project_durability      TEXT DEFAULT NULL,
    project_transferability TEXT DEFAULT NULL,
    CONSTRAINT fk_project_long_term_plans_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
