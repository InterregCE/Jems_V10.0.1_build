CREATE TABLE project_description
(
    project_id                   INTEGER PRIMARY KEY,
    project_coordination         TEXT DEFAULT NULL,
    project_quality_assurance    TEXT DEFAULT NULL,
    project_communication        TEXT DEFAULT NULL,
    project_financial_management TEXT DEFAULT NULL,
    project_joint_development    TEXT DEFAULT NULL,
    project_joint_implementation TEXT DEFAULT NULL,
    project_joint_staffing       TEXT DEFAULT NULL,
    project_joint_financing      TEXT DEFAULT NULL,
    project_ownership            TEXT DEFAULT NULL,
    project_durability           TEXT DEFAULT NULL,
    project_transferability      TEXT DEFAULT NULL,
    CONSTRAINT fk_project_description_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_horizontal_principles
(
    project_id                              INTEGER PRIMARY KEY,
    sustainable_development_criteria_effect VARCHAR(20) DEFAULT NULL,
    sustainable_development_description     TEXT        DEFAULT NULL,
    equal_opportunities_effect              VARCHAR(20) DEFAULT NULL,
    equal_opportunities_description         TEXT        DEFAULT NULL,
    sexual_equality_effect                  VARCHAR(20) DEFAULT NULL,
    sexual_equality_description             TEXT        DEFAULT NULL,

    CONSTRAINT fk_project_horizontal_principles_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
