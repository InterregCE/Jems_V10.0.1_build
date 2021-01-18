CREATE TABLE project_description_c7_management_transl
(
    project_id                               INT UNSIGNED NOT NULL,
    language                                 VARCHAR(3)   NOT NULL,
    project_coordination                     TEXT DEFAULT NULL,
    project_quality_assurance                TEXT DEFAULT NULL,
    project_communication                    TEXT DEFAULT NULL,
    project_financial_management             TEXT DEFAULT NULL,
    project_joint_development_description    TEXT DEFAULT NULL,
    project_joint_implementation_description TEXT DEFAULT NULL,
    project_joint_staffing_description       TEXT DEFAULT NULL,
    project_joint_financing_description      TEXT DEFAULT NULL,
    sustainable_development_description      TEXT DEFAULT NULL,
    equal_opportunities_description          TEXT DEFAULT NULL,
    sexual_equality_description              TEXT DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_descr_c7_mgmnt_transl_to_project_descr_c7_mgmnt
        FOREIGN KEY (project_id)
            REFERENCES project_description_c7_management (project_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_description_c7_management
    DROP COLUMN project_coordination;
ALTER TABLE project_description_c7_management
    DROP COLUMN project_quality_assurance;
ALTER TABLE project_description_c7_management
    DROP COLUMN project_communication;
ALTER TABLE project_description_c7_management
    DROP COLUMN project_financial_management;
ALTER TABLE project_description_c7_management
    DROP COLUMN project_joint_development_description;
ALTER TABLE project_description_c7_management
    DROP COLUMN project_joint_implementation_description;
ALTER TABLE project_description_c7_management
    DROP COLUMN project_joint_staffing_description;
ALTER TABLE project_description_c7_management
    DROP COLUMN project_joint_financing_description;
ALTER TABLE project_description_c7_management
    DROP COLUMN sustainable_development_description;
ALTER TABLE project_description_c7_management
    DROP COLUMN equal_opportunities_description;
ALTER TABLE project_description_c7_management
    DROP COLUMN sexual_equality_description;

CREATE TABLE project_description_c8_long_term_plans_transl
(
    project_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3)   NOT NULL,
    project_ownership       TEXT DEFAULT NULL,
    project_durability      TEXT DEFAULT NULL,
    project_transferability TEXT DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_descr_c8_plans_transl_to_project_descr_c8_plans
        FOREIGN KEY (project_id)
            REFERENCES project_description_c8_long_term_plans (project_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_description_c8_long_term_plans
    DROP COLUMN project_ownership;
ALTER TABLE project_description_c8_long_term_plans
    DROP COLUMN project_durability;
ALTER TABLE project_description_c8_long_term_plans
    DROP COLUMN project_transferability;