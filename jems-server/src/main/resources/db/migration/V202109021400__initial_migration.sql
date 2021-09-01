CREATE TABLE account_role
(
    id   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(127) NOT NULL UNIQUE
);

CREATE TABLE account
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    surname         VARCHAR(255) NOT NULL,
    account_role_id INT UNSIGNED NOT NULL,
    password        VARCHAR(255) NOT NULL,
    CONSTRAINT fk_account_to_account_role
        FOREIGN KEY (account_role_id) REFERENCES account_role (id)
);

CREATE TABLE account_role_permission
(
    account_role_id INT UNSIGNED NOT NULL,
    permission      VARCHAR(255) NOT NULL,
    PRIMARY KEY (account_role_id, permission),
    CONSTRAINT fk_account_role_permission_to_account_role
        FOREIGN KEY (account_role_id) REFERENCES account_role (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT INTO account_role (id, name)
VALUES (1, 'administrator'),
       (2, 'programme user'),
       (3, 'applicant user');

INSERT INTO account (email, name, surname, account_role_id, password)
VALUES ('admin@jems.eu', 'Admin', 'Admin', 1, '{bcrypt}$2a$10$U7oTeVv4GXWmZzL0lE1H8eX4.TpJyhwz6SlKefFKnb3VDLoivO8sC');

SELECT id INTO @id FROM account_role WHERE `name` = 'administrator' ORDER BY id DESC LIMIT 1;
INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectRetrieve'),
       (@id, 'ProjectCheckApplicationForm'),
       (@id, 'ProjectSubmission'),
       (@id, 'RoleRetrieve'),
       (@id, 'RoleCreate'),
       (@id, 'RoleUpdate'),
       (@id, 'UserRetrieve'),
       (@id, 'UserCreate'),
       (@id, 'UserUpdate'),
       (@id, 'UserUpdateRole'),
       (@id, 'UserUpdatePassword'),
       (@id, 'AuditRetrieve'),
       (@id, 'ProgrammeSetupRetrieve'),
       (@id, 'ProgrammeSetupUpdate'),
       (@id, 'CallRetrieve'),
       (@id, 'CallUpdate'),
       (@id, 'ProjectAssessmentView'),
       (@id, 'ProjectAssessmentEligibilityEnter'),
       (@id, 'ProjectAssessmentQualityEnter'),
       (@id, 'ProjectStatusDecideEligible'),
       (@id, 'ProjectStatusDecideIneligible'),
       (@id, 'ProjectStatusDecideApproved'),
       (@id, 'ProjectStatusDecideApprovedWithConditions'),
       (@id, 'ProjectStatusDecideNotApproved'),
       (@id, 'ProjectStartStepTwo'),
       (@id, 'CallPublishedRetrieve'),
       (@id, 'ProjectsWithOwnershipRetrieve'),
       (@id, 'ProjectCreate'),
       (@id, 'ProjectFileApplicationRetrieve'),
       (@id, 'ProjectFileApplicationUpdate'),
       (@id, 'ProjectFileAssessmentRetrieve'),
       (@id, 'ProjectFileAssessmentUpdate'),
       (@id, 'ProjectStatusReturnToApplicant'),
       (@id, 'ProjectStatusDecisionRevert'),
       (@id, 'ProjectFormRetrieve'),
       (@id, 'ProjectFormUpdate');

SELECT id INTO @id FROM account_role WHERE `name` = 'programme user' ORDER BY id DESC LIMIT 1;
INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'ProjectRetrieve'),
       (@id, 'ProjectCheckApplicationForm'),
       (@id, 'AuditRetrieve'),
       (@id, 'ProgrammeSetupRetrieve'),
       (@id, 'ProgrammeSetupUpdate'),
       (@id, 'CallRetrieve'),
       (@id, 'CallUpdate'),
       (@id, 'ProjectAssessmentView'),
       (@id, 'ProjectAssessmentEligibilityEnter'),
       (@id, 'ProjectAssessmentQualityEnter'),
       (@id, 'ProjectStatusDecideEligible'),
       (@id, 'ProjectStatusDecideIneligible'),
       (@id, 'ProjectStatusDecideApproved'),
       (@id, 'ProjectStatusDecideApprovedWithConditions'),
       (@id, 'ProjectStatusDecideNotApproved'),
       (@id, 'ProjectStartStepTwo'),
       (@id, 'CallPublishedRetrieve'),
       (@id, 'ProjectsWithOwnershipRetrieve'),
       (@id, 'ProjectFileApplicationRetrieve'),
       (@id, 'ProjectFileApplicationUpdate'),
       (@id, 'ProjectFileAssessmentRetrieve'),
       (@id, 'ProjectFileAssessmentUpdate'),
       (@id, 'ProjectStatusReturnToApplicant'),
       (@id, 'ProjectStatusDecisionRevert'),
       (@id, 'ProjectFormRetrieve');

SELECT id INTO @id FROM account_role WHERE `name` = 'applicant user' ORDER BY id DESC LIMIT 1;
INSERT INTO account_role_permission(account_role_id, permission)
VALUES (@id, 'CallPublishedRetrieve'),
       (@id, 'ProjectsWithOwnershipRetrieve'),
       (@id, 'ProjectCreate');


DELIMITER $$

CREATE TRIGGER protect_last_system_admin_deletion
    BEFORE DELETE
    ON account
    FOR EACH ROW
BEGIN
    DECLARE count_of_admins INTEGER;

    SELECT COUNT(*) FROM account WHERE account_role_id = 1 INTO count_of_admins;

    IF OLD.account_role_id = 1 AND count_of_admins <= 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'user.lastAdmin.cannot.be.removed';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER protect_last_system_admin_role_update
    BEFORE UPDATE
    ON account
    FOR EACH ROW
BEGIN
    DECLARE count_of_admins INTEGER;

    SELECT COUNT(*) FROM account WHERE account_role_id = 1 INTO count_of_admins;

    IF OLD.account_role_id = 1 AND NEW.account_role_id != 1 AND count_of_admins <= 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'user.lastAdmin.cannot.be.removed';
    END IF;
END$$

DELIMITER ;

CREATE TABLE account_profile
(
    account_id INT UNSIGNED NOT NULL PRIMARY KEY,
    language   VARCHAR(50) DEFAULT NULL,
    CONSTRAINT fk_account_profile_to_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE CASCADE
);

CREATE TABLE nuts_country
(
    id    VARCHAR(2) PRIMARY KEY,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE nuts_region_1
(
    id              VARCHAR(3) PRIMARY KEY,
    nuts_country_id VARCHAR(2)   NOT NULL,
    title           VARCHAR(255) NOT NULL,
    CONSTRAINT fk_nuts_region_1_to_nuts_country
        FOREIGN KEY (nuts_country_id) REFERENCES nuts_country (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE nuts_region_2
(
    id               VARCHAR(4) PRIMARY KEY,
    nuts_region_1_id VARCHAR(3)   NOT NULL,
    title            VARCHAR(255) NOT NULL,
    CONSTRAINT fk_nuts_region_2_to_nuts_region_1
        FOREIGN KEY (nuts_region_1_id) REFERENCES nuts_region_1 (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE nuts_region_3
(
    id               VARCHAR(5) PRIMARY KEY,
    nuts_region_2_id VARCHAR(4)   NOT NULL,
    title            VARCHAR(255) NOT NULL,
    CONSTRAINT fk_nuts_region_3_to_nuts_region_2
        FOREIGN KEY (nuts_region_2_id) REFERENCES nuts_region_2 (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE nuts_metadata
(
    id         ENUM ('1') PRIMARY KEY,
    nuts_date  DATE         DEFAULT NULL,
    nuts_title VARCHAR(127) DEFAULT NULL
);

INSERT INTO nuts_metadata (id) VALUE (1);

CREATE TABLE programme_nuts
(
    nuts_region_3_id  VARCHAR(5) PRIMARY KEY,
    programme_data_id ENUM ('1') DEFAULT '1',
    CONSTRAINT fk_programme_nuts_to_nuts_region_3
        FOREIGN KEY (nuts_region_3_id) REFERENCES nuts_region_3 (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

CREATE TABLE programme_data
(
    id                                 ENUM ('1') DEFAULT '1' PRIMARY KEY,
    cci                                VARCHAR(15),
    title                              VARCHAR(255),
    version                            VARCHAR(255),
    first_year                         INT,
    last_year                          INT,
    eligible_from                      DATE,
    eligible_until                     DATE,
    commission_decision_number         VARCHAR(255),
    commission_decision_date           DATE,
    programme_amending_decision_number VARCHAR(255),
    programme_amending_decision_date   DATE,
    default_user_role_id               INT UNSIGNED DEFAULT NULL,
    project_id_programme_abbreviation  VARCHAR(12) DEFAULT NULL,
    project_id_use_call_id             BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_to_account_role
        FOREIGN KEY (default_user_role_id) REFERENCES account_role (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

SELECT id INTO @id FROM account_role WHERE `name` = 'applicant user' ORDER BY id DESC LIMIT 1;
INSERT INTO programme_data (id, default_user_role_id)
VALUES ('1', @id);

CREATE TABLE programme_fund
(
    id       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    selected BOOLEAN NOT NULL DEFAULT FALSE,
    type     ENUM (
        'ERDF',
        'IPA III CBC',
        'Neighbourhood CBC',
        'IPA III',
        'NDICI',
        'OCTP',
        'Interreg Funds',
        'Other')     NOT NULL DEFAULT 'Other'
);

CREATE TABLE programme_fund_transl
(
    source_entity_id      INT UNSIGNED NOT NULL,
    language     VARCHAR(3)   NOT NULL,
    abbreviation VARCHAR(127) DEFAULT NULL,
    description  VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_programme_fund_transl_to_programme_fund
        FOREIGN KEY (source_entity_id) REFERENCES programme_fund (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT INTO programme_fund (type) VALUE ('ERDF');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(source_entity_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'ERDF', 'Territorial cooperation Goal (Interreg)');

INSERT INTO programme_fund (type) VALUE ('IPA III CBC');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(source_entity_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'IPA III CBC', 'Interreg A, external cross-border cooperation');

INSERT INTO programme_fund (type) VALUE ('Neighbourhood CBC');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(source_entity_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'Neighbourhood CBC', 'Interreg A, external cross-border cooperation');

INSERT INTO programme_fund (type) VALUE ('IPA III');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(source_entity_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'IPA III', 'Interreg B and C');

INSERT INTO programme_fund (type) VALUE ('NDICI');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(source_entity_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'NDICI', 'Interreg B and C');

INSERT INTO programme_fund (type) VALUE ('OCTP');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(source_entity_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'OCTP', 'Interreg C and D');

INSERT INTO programme_fund (type) VALUE ('Interreg Funds');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(source_entity_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'Interreg Funds', 'ERDF, IPA III, NDICI or OCTP, where as single amount under Interreg B and C');

CREATE TABLE programme_objective
(
    code VARCHAR(7) PRIMARY KEY
);

INSERT INTO programme_objective (code)
VALUES ('PO1'),
       ('PO2'),
       ('PO3'),
       ('PO4'),
       ('PO5'),
       ('ISO1'),
       ('ISO2'),
       ('ISO12');

CREATE TABLE programme_objective_policy
(
    objective_id VARCHAR(7) NOT NULL,
    code         VARCHAR(127) PRIMARY KEY,
    CONSTRAINT fk_programme_objective_policy_programme_objective
        FOREIGN KEY (objective_id) REFERENCES programme_objective (code)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
);

INSERT INTO programme_objective_policy (objective_id, code)
VALUES ('PO1', 'AdvancedTechnologies'),
       ('PO1', 'Digitisation'),
       ('PO1', 'Growth'),
       ('PO1', 'IndustrialTransition'),
       ('PO1', 'DigitalConnectivity'),

       ('PO2', 'EnergyEfficiency'),
       ('PO2', 'RenewableEnergy'),
       ('PO2', 'SmartEnergy'),
       ('PO2', 'ClimateChange'),
       ('PO2', 'WaterManagement'),
       ('PO2', 'CircularEconomy'),
       ('PO2', 'GreenInfrastructure'),
       ('PO2', 'ZeroCarbonEconomy'),

       ('PO3', 'InterModalTenT'),
       ('PO3', 'CrossBorderMobility'),

       ('PO4', 'SocialInfrastructure'),
       ('PO4', 'QualityInEducation'),
       ('PO4', 'DisadvantagedGroups'),
       ('PO4', 'IntegratedActionsForMigrants'),
       ('PO4', 'Healthcare'),
       ('PO4', 'CultureAndTourism'),
       ('PO4', 'PeacePlus'),
       ('PO4', 'JobSeekers'),
       ('PO4', 'LabourMarketMatching'),
       ('PO4', 'GenderBalance'),
       ('PO4', 'HealthyAgeing'),
       ('PO4', 'DualTrainingSystems'),
       ('PO4', 'EqualAccess'),
       ('PO4', 'LifelongLearning'),
       ('PO4', 'EqualOpportunities'),
       ('PO4', 'IntegrationOfThirdCountryNationals'),
       ('PO4', 'IntegrationOfMarginalised'),
       ('PO4', 'AffordableServices'),
       ('PO4', 'SocialIntegration'),
       ('PO4', 'MaterialAssistance'),

       ('PO5', 'EnvDevelopment'),
       ('PO5', 'LocalEnvDevelopment'),

       ('ISO1', 'ISO1PublicAuthorities'),
       ('ISO1', 'ISO1AdministrativeCooperation'),
       ('ISO1', 'ISO1MutualTrust'),
       ('ISO1', 'ISO1MacroRegion'),
       ('ISO1', 'ISO1Democracy'),
       ('ISO1', 'ISO1Other'),

       ('ISO2', 'ISO2BorderCrossing'),
       ('ISO2', 'ISO2MobilityMigration'),
       ('ISO2', 'ISO2InternationalProtection'),
       ('ISO2', 'ISO2Other'),

       ('ISO12', 'ISO12PublicAuthorities'),
       ('ISO12', 'ISO12PromotingCooperation'),
       ('ISO12', 'ISO12MutualTrust'),
       ('ISO12', 'ISO12MacroRegion'),
       ('ISO12', 'ISO12Democracy'),
       ('ISO12', 'ISO12BorderCrossing'),
       ('ISO12', 'ISO12MobilityMigration'),
       ('ISO12', 'ISO12InternationalProtection'),
       ('ISO12', 'ISO12Other');

CREATE TABLE programme_priority
(
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(50)  NOT NULL UNIQUE,
    objective_id VARCHAR(7)   NOT NULL,
    CONSTRAINT fk_programme_priority_programme_objective
        FOREIGN KEY (objective_id) REFERENCES programme_objective (code)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
);

CREATE TABLE programme_priority_transl
(
    programme_priority_id INT UNSIGNED NOT NULL,
    language              VARCHAR(3)   NOT NULL,
    title                 TEXT(300) DEFAULT NULL,
    PRIMARY KEY (programme_priority_id, language),
    CONSTRAINT fk_programme_priority_transl_to_programme_priority
        FOREIGN KEY (programme_priority_id)
            REFERENCES programme_priority (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE programme_priority_specific_objective
(
    programme_objective_policy_code VARCHAR(127) PRIMARY KEY,
    programme_priority_id           INT UNSIGNED NOT NULL,
    code                            VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_programme_priority_policy_programme_objective_policy
        FOREIGN KEY (programme_objective_policy_code) REFERENCES programme_objective_policy (code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_programme_priority_policy_programme_priority
        FOREIGN KEY (programme_priority_id) REFERENCES programme_priority (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE programme_indicator_result
(
    id                           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    identifier                   VARCHAR(5) NOT NULL,
    code                         VARCHAR(6)                       DEFAULT NULL,
    programme_priority_policy_id VARCHAR(127)                     DEFAULT NULL,
    baseline                     DECIMAL(11, 2) UNSIGNED ZEROFILL DEFAULT NULL,
    reference_year               VARCHAR(10)                      DEFAULT NULL,
    final_target                 DECIMAL(11, 2)                   DEFAULT NULL,
    comment                      TEXT                             DEFAULT NULL,
    CONSTRAINT fk_programme_indicator_result_to_programme_priority_so
        FOREIGN KEY (programme_priority_policy_id) REFERENCES programme_priority_specific_objective (programme_objective_policy_code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

CREATE TABLE programme_indicator_result_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    name             TEXT(255) DEFAULT NULL,
    measurement_unit TEXT(255) DEFAULT NULL,
    source_of_data   TEXT DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_programme_indicator_result_transl_to_indicator_result FOREIGN KEY (source_entity_id) REFERENCES programme_indicator_result (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE programme_indicator_output
(
    id                           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    identifier                   VARCHAR(5) NOT NULL,
    code                         VARCHAR(6)     DEFAULT NULL,
    programme_priority_policy_id VARCHAR(127)   DEFAULT NULL,
    milestone                    DECIMAL(11, 2) DEFAULT NULL,
    final_target                 DECIMAL(11, 2) DEFAULT NULL,
    result_indicator_id          INT UNSIGNED   DEFAULT NULL,
    CONSTRAINT fk_programme_indicator_output_to_programme_priority_so
        FOREIGN KEY (programme_priority_policy_id) REFERENCES programme_priority_specific_objective (programme_objective_policy_code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_programme_indicator_output_to_programme_indicator_result
        FOREIGN KEY (result_indicator_id)
            REFERENCES programme_indicator_result (id)
            ON DELETE SET NULL
);

CREATE TABLE programme_indicator_output_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    name             TEXT(255) DEFAULT NULL,
    measurement_unit TEXT(255) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_programme_indicator_output_transl_to_indicator_output FOREIGN KEY (source_entity_id) REFERENCES programme_indicator_output (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE programme_strategy
(
    strategy VARCHAR(127) PRIMARY KEY,
    active   BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO programme_strategy (strategy)
VALUES ('EUStrategyAdriaticIonianRegion'),
       ('EUStrategyAlpineRegion'),
       ('EUStrategyBalticSeaRegion'),
       ('EUStrategyDanubeRegion'),
       ('SeaBasinStrategyNorthSea'),
       ('SeaBasinStrategyBlackSea'),
       ('SeaBasinStrategyBalticSea'),
       ('SeaBasinStrategyArcticOcean'),
       ('SeaBasinStrategyOutermostRegions'),
       ('SeaBasinStrategyAdriaticIonianSea'),
       ('MediterraneanSeaBasin'),
       ('AtlanticStrategy'),
       ('EuropeanGreenDeal'),
       ('TerritorialAgenda2030');

CREATE TABLE project_call
(
    id                                               INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    creator_id                                       INT UNSIGNED NOT NULL,
    name                                             VARCHAR(255) NOT NULL UNIQUE,
    status                                           VARCHAR(127) NOT NULL,
    start_date                                       DATETIME(3) NOT NULL,
    end_date                                         DATETIME(3) NOT NULL,
    end_date_step1                                   DATETIME(3) DEFAULT NULL,
    length_of_period                                 TINYINT UNSIGNED NOT NULL,
    is_additional_fund_allowed                       BOOLEAN      NOT NULL DEFAULT FALSE,
    allow_real_staff_costs                           BOOLEAN      NOT NULL DEFAULT TRUE,
    allow_real_travel_and_accommodation_costs        BOOLEAN      NOT NULL DEFAULT TRUE,
    allow_real_external_expertise_and_services_costs BOOLEAN      NOT NULL DEFAULT TRUE,
    allow_real_equipment_costs                       BOOLEAN      NOT NULL DEFAULT TRUE,
    allow_real_infrastructure_costs                  BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_call_creator_user
        FOREIGN KEY (creator_id) REFERENCES account (id)
);

CREATE TABLE project_call_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language        VARCHAR(3)   NOT NULL,
    description     TEXT(1000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_call_transl_to_project_call
        FOREIGN KEY (source_entity_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project
(
    id                                         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    custom_identifier                          VARCHAR(31)  DEFAULT NULL UNIQUE,
    project_call_id                            INT UNSIGNED NOT NULL,
    programme_priority_policy_objective_policy VARCHAR(127) DEFAULT NULL,
    acronym                                    VARCHAR(25)  NOT NULL,
    applicant_id                               INT UNSIGNED NOT NULL,
    duration                                   INTEGER      DEFAULT NULL,
    CONSTRAINT fk_project_project_call
        FOREIGN KEY (project_call_id) REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_to_programme_so
        FOREIGN KEY (programme_priority_policy_objective_policy)
            REFERENCES programme_priority_specific_objective (programme_objective_policy_code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_applicant_user
        FOREIGN KEY (applicant_id) REFERENCES account (id)
);

CREATE TABLE project_transl
(
    project_id INT UNSIGNED NOT NULL,
    language   VARCHAR(3)   NOT NULL,
    title      VARCHAR(200),
    intro      TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_transl_to_project
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_status
(
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id    INT UNSIGNED                             NULL,
    status        VARCHAR(127)                             NOT NULL,
    account_id    INT UNSIGNED                             NOT NULL,
    updated       DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    decision_date DATE                                     NULL,
    note          TEXT                                     NULL,
    CONSTRAINT fk_project_status_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_status_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

ALTER TABLE project
    ADD COLUMN project_status_id             INT UNSIGNED NOT NULL AFTER applicant_id,
    ADD COLUMN first_submission_id           INT UNSIGNED DEFAULT NULL AFTER project_status_id,
    ADD COLUMN last_resubmission_id          INT UNSIGNED DEFAULT NULL AFTER first_submission_id,
    ADD COLUMN eligibility_decision_step1_id INT UNSIGNED DEFAULT NULL AFTER last_resubmission_id,
    ADD COLUMN funding_decision_step1_id     INT UNSIGNED DEFAULT NULL AFTER eligibility_decision_step1_id,
    ADD COLUMN eligibility_decision_id       INT UNSIGNED DEFAULT NULL AFTER funding_decision_step1_id,
    ADD COLUMN funding_pre_decision_id       INT UNSIGNED DEFAULT NULL AFTER eligibility_decision_id,
    ADD COLUMN funding_final_decision_id     INT UNSIGNED DEFAULT NULL AFTER funding_pre_decision_id,

    ADD CONSTRAINT fk_project_to_project_status
        FOREIGN KEY (project_status_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_project_first_submission_project_status
        FOREIGN KEY (first_submission_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_project_last_resubmission_project_status
        FOREIGN KEY (last_resubmission_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_eligibility_decision_step1_to_project_status
        FOREIGN KEY (eligibility_decision_step1_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_funding_decision_step1_to_project_status
        FOREIGN KEY (funding_decision_step1_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_eligibility_decision_to_project_status
        FOREIGN KEY (eligibility_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_funding_pre_decision_to_project_status
        FOREIGN KEY (funding_pre_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_funding_final_decision_id_to_project_status
        FOREIGN KEY (funding_final_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD SYSTEM VERSIONING;

CREATE TABLE project_version
(
    version    VARCHAR(127)          NOT NULL,
    project_id INT UNSIGNED          NOT NULL,
    account_id INT UNSIGNED          NOT NULL,
    created_at DATETIME(6)           NOT NULL,
    row_end    DATETIME(6) INVISIBLE NOT NULL DEFAULT localtimestamp,
    status     VARCHAR(127)          NOT NULL,
    PRIMARY KEY (project_id, version)
);

DELIMITER $$

CREATE TRIGGER protect_resetting_project_from_status
    BEFORE UPDATE
    ON project_status
    FOR EACH ROW
BEGIN
    IF NEW.project_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =
                'project cannot be removed from the status, it can be null only when created initially';
    END IF;
END$$

DELIMITER ;

CREATE TABLE project_call_priority_specific_objective
(
    programme_specific_objective VARCHAR(127) NOT NULL,
    call_id                   INT UNSIGNED NOT NULL,
    CONSTRAINT fk_project_call_priority_so_to_programme_priority_so
        FOREIGN KEY (programme_specific_objective)
            REFERENCES programme_priority_specific_objective (programme_objective_policy_code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_priority_so_to_project_call
        FOREIGN KEY (call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT pk_project_call_priority_so PRIMARY KEY (programme_specific_objective, call_id)
);

CREATE TABLE project_call_strategy
(
    programme_strategy VARCHAR(127) NOT NULL,
    call_id            INT UNSIGNED NOT NULL,
    PRIMARY KEY (programme_strategy, call_id),
    CONSTRAINT fk_project_call_strategy_to_call
        FOREIGN KEY (call_id) REFERENCES project_call (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_call_strategy_to_programme_strategy
        FOREIGN KEY (programme_strategy) REFERENCES programme_strategy (strategy)
);

CREATE TABLE project_assessment_eligibility
(
    project_id INT UNSIGNED,
    step       SMALLINT UNSIGNED NOT NULL,
    result     ENUM('PASSED', 'FAILED') NOT NULL,
    account_id INT UNSIGNED                             NOT NULL,
    updated    DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    note       VARCHAR(1000)                            NULL,
    PRIMARY KEY (project_id, step),
    CONSTRAINT fk_project_assessment_eligibility_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_assessment_eligibility_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_assessment_quality
(
    project_id INT UNSIGNED,
    step       SMALLINT UNSIGNED NOT NULL,
    result     ENUM('RECOMMENDED_FOR_FUNDING', 'RECOMMENDED_WITH_CONDITIONS', 'NOT_RECOMMENDED') NOT NULL,
    account_id INT UNSIGNED NOT NULL,
    updated    DATETIME(3) NOT NULL DEFAULT current_timestamp (3),
    note       VARCHAR(1000) NULL,
    PRIMARY KEY (project_id, step),
    CONSTRAINT fk_project_assessment_quality_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_assessment_quality_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_file
(
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    project_id  INT UNSIGNED NOT NULL,
    user_id     INT UNSIGNED NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    size        BIGINT       NOT NULL,
    updated     DATETIME(3) DEFAULT CURRENT_TIMESTAMP (3) NOT NULL,
    CONSTRAINT fk_project_file_to_account
        FOREIGN KEY (user_id) REFERENCES account (id),
    CONSTRAINT fk_project_file_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_file_category
(
    file_id INT UNSIGNED,
    type    VARCHAR(255) NOT NULL,
    PRIMARY KEY (file_id, type),
    CONSTRAINT fk_project_file_category_to_project_file
        FOREIGN KEY (file_id) REFERENCES project_file (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_partner
(
    id                        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id                INT UNSIGNED NOT NULL,
    abbreviation              VARCHAR(15)  NOT NULL,
    role                      VARCHAR(127) NOT NULL,
    sort_number               INT          DEFAULT NULL,
    name_in_original_language VARCHAR(127) DEFAULT NULL,
    name_in_english           VARCHAR(127) DEFAULT NULL,
    partner_type              VARCHAR(127) DEFAULT NULL,
    vat                       VARCHAR(50)  DEFAULT NULL,
    vat_recovery              ENUM ('Yes', 'No', 'Partly') DEFAULT NULL,
    partner_sub_type          ENUM ('MICRO_ENTERPRISE','SMALL_ENTERPRISE', 'MEDIUM_SIZED_ENTERPRISE', 'LARGE_ENTERPRISE') DEFAULT NULL,
    nace                      ENUM ('A','A_01','A_01_1','A_01_11','A_01_12','A_01_13','A_01_14','A_01_15','A_01_16','A_01_19','A_01_2','A_01_21','A_01_22','A_01_23','A_01_24','A_01_25','A_01_26','A_01_27','A_01_28','A_01_29','A_01_3','A_01_30','A_01_4','A_01_41','A_01_42','A_01_43','A_01_44','A_01_45','A_01_46','A_01_47','A_01_49','A_01_5','A_01_50','A_01_6','A_01_61','A_01_62','A_01_63','A_01_64','A_01_7','A_01_70','A_02','A_02_1','A_02_10','A_02_2','A_02_20','A_02_3','A_02_30','A_02_4','A_02_40','A_03','A_03_1','A_03_11','A_03_12','A_03_2','A_03_21','A_03_22','B','B_05','B_05_1','B_05_10','B_05_2','B_05_20','B_06','B_06_1','B_06_10','B_06_2','B_06_20','B_07','B_07_1','B_07_10','B_07_2','B_07_21','B_07_29','B_08','B_08_1','B_08_11','B_08_12','B_08_9','B_08_91','B_08_92','B_08_93','B_08_99','B_09','B_09_1','B_09_10','B_09_9','B_09_90','C','C_10','C_10_1','C_10_11','C_10_12','C_10_13','C_10_2','C_10_20','C_10_3','C_10_31','C_10_32','C_10_39','C_10_4','C_10_41','C_10_42','C_10_5','C_10_51','C_10_52','C_10_6','C_10_61','C_10_62','C_10_7','C_10_71','C_10_72','C_10_73','C_10_8','C_10_81','C_10_82','C_10_83','C_10_84','C_10_85','C_10_86','C_10_89','C_10_9','C_10_91','C_10_92','C_11','C_11_0','C_11_01','C_11_02','C_11_03','C_11_04','C_11_05','C_11_06','C_11_07','C_12','C_12_0','C_12_00','C_13','C_13_1','C_13_10','C_13_2','C_13_20','C_13_3','C_13_30','C_13_9','C_13_91','C_13_92','C_13_93','C_13_94','C_13_95','C_13_96','C_13_99','C_14','C_14_1','C_14_11','C_14_12','C_14_13','C_14_14','C_14_19','C_14_2','C_14_20','C_14_3','C_14_31','C_14_39','C_15','C_15_1','C_15_11','C_15_12','C_15_2','C_15_20','C_16','C_16_1','C_16_10','C_16_2','C_16_21','C_16_22','C_16_23','C_16_24','C_16_29','C_17','C_17_1','C_17_11','C_17_12','C_17_2','C_17_21','C_17_22','C_17_23','C_17_24','C_17_29','C_18','C_18_1','C_18_11','C_18_12','C_18_13','C_18_14','C_18_2','C_18_20','C_19','C_19_1','C_19_10','C_19_2','C_19_20','C_20','C_20_1','C_20_11','C_20_12','C_20_13','C_20_14','C_20_15','C_20_16','C_20_17','C_20_2','C_20_20','C_20_3','C_20_30','C_20_4','C_20_41','C_20_42','C_20_5','C_20_51','C_20_52','C_20_53','C_20_59','C_20_6','C_20_60','C_21','C_21_1','C_21_10','C_21_2','C_21_20','C_22','C_22_1','C_22_11','C_22_19','C_22_2','C_22_21','C_22_22','C_22_23','C_22_29','C_23','C_23_1','C_23_11','C_23_12','C_23_13','C_23_14','C_23_19','C_23_2','C_23_20','C_23_3','C_23_31','C_23_32','C_23_4','C_23_41','C_23_42','C_23_43','C_23_44','C_23_49','C_23_5','C_23_51','C_23_52','C_23_6','C_23_61','C_23_62','C_23_63','C_23_64','C_23_65','C_23_69','C_23_7','C_23_70','C_23_9','C_23_91','C_23_99','C_24','C_24_1','C_24_10','C_24_2','C_24_20','C_24_3','C_24_31','C_24_32','C_24_33','C_24_34','C_24_4','C_24_41','C_24_42','C_24_43','C_24_44','C_24_45','C_24_46','C_24_5','C_24_51','C_24_52','C_24_53','C_24_54','C_25','C_25_1','C_25_11','C_25_12','C_25_2','C_25_21','C_25_29','C_25_3','C_25_30','C_25_4','C_25_40','C_25_5','C_25_50','C_25_6','C_25_61','C_25_62','C_25_7','C_25_71','C_25_72','C_25_73','C_25_9','C_25_91','C_25_92','C_25_93','C_25_94','C_25_99','C_26','C_26_1','C_26_11','C_26_12','C_26_2','C_26_20','C_26_3','C_26_30','C_26_4','C_26_40','C_26_5','C_26_51','C_26_52','C_26_6','C_26_60','C_26_7','C_26_70','C_26_8','C_26_80','C_27','C_27_1','C_27_11','C_27_12','C_27_2','C_27_20','C_27_3','C_27_31','C_27_32','C_27_33','C_27_4','C_27_40','C_27_5','C_27_51','C_27_52','C_27_9','C_27_90','C_28','C_28_1','C_28_11','C_28_12','C_28_13','C_28_14','C_28_15','C_28_2','C_28_21','C_28_22','C_28_23','C_28_24','C_28_25','C_28_29','C_28_3','C_28_30','C_28_4','C_28_41','C_28_49','C_28_9','C_28_91','C_28_92','C_28_93','C_28_94','C_28_95','C_28_96','C_28_99','C_29','C_29_1','C_29_10','C_29_2','C_29_20','C_29_3','C_29_31','C_29_32','C_30','C_30_1','C_30_11','C_30_12','C_30_2','C_30_20','C_30_3','C_30_30','C_30_4','C_30_40','C_30_9','C_30_91','C_30_92','C_30_99','C_31','C_31_0','C_31_01','C_31_02','C_31_03','C_31_09','C_32','C_32_1','C_32_11','C_32_12','C_32_13','C_32_2','C_32_20','C_32_3','C_32_30','C_32_4','C_32_40','C_32_5','C_32_50','C_32_9','C_32_91','C_32_99','C_33','C_33_1','C_33_11','C_33_12','C_33_13','C_33_14','C_33_15','C_33_16','C_33_17','C_33_19','C_33_2','C_33_20','D','D_35','D_35_1','D_35_11','D_35_12','D_35_13','D_35_14','D_35_2','D_35_21','D_35_22','D_35_23','D_35_3','D_35_30','E','E_36','E_36_0','E_36_00','E_37','E_37_0','E_37_00','E_38','E_38_1','E_38_11','E_38_12','E_38_2','E_38_21','E_38_22','E_38_3','E_38_31','E_38_32','E_39','E_39_0','E_39_00','F','F_41','F_41_1','F_41_10','F_41_2','F_41_20','F_42','F_42_1','F_42_11','F_42_12','F_42_13','F_42_2','F_42_21','F_42_22','F_42_9','F_42_91','F_42_99','F_43','F_43_1','F_43_11','F_43_12','F_43_13','F_43_2','F_43_21','F_43_22','F_43_29','F_43_3','F_43_31','F_43_32','F_43_33','F_43_34','F_43_39','F_43_9','F_43_91','F_43_99','G','G_45','G_45_1','G_45_11','G_45_19','G_45_2','G_45_20','G_45_3','G_45_31','G_45_32','G_45_4','G_45_40','G_46','G_46_1','G_46_11','G_46_12','G_46_13','G_46_14','G_46_15','G_46_16','G_46_17','G_46_18','G_46_19','G_46_2','G_46_21','G_46_22','G_46_23','G_46_24','G_46_3','G_46_31','G_46_32','G_46_33','G_46_34','G_46_35','G_46_36','G_46_37','G_46_38','G_46_39','G_46_4','G_46_41','G_46_42','G_46_43','G_46_44','G_46_45','G_46_46','G_46_47','G_46_48','G_46_49','G_46_5','G_46_51','G_46_52','G_46_6','G_46_61','G_46_62','G_46_63','G_46_64','G_46_65','G_46_66','G_46_69','G_46_7','G_46_71','G_46_72','G_46_73','G_46_74','G_46_75','G_46_76','G_46_77','G_46_9','G_46_90','G_47','G_47_1','G_47_11','G_47_19','G_47_2','G_47_21','G_47_22','G_47_23','G_47_24','G_47_25','G_47_26','G_47_29','G_47_3','G_47_30','G_47_4','G_47_41','G_47_42','G_47_43','G_47_5','G_47_51','G_47_52','G_47_53','G_47_54','G_47_59','G_47_6','G_47_61','G_47_62','G_47_63','G_47_64','G_47_65','G_47_7','G_47_71','G_47_72','G_47_73','G_47_74','G_47_75','G_47_76','G_47_77','G_47_78','G_47_79','G_47_8','G_47_81','G_47_82','G_47_89','G_47_9','G_47_91','G_47_99','H','H_49','H_49_1','H_49_10','H_49_2','H_49_20','H_49_3','H_49_31','H_49_32','H_49_39','H_49_4','H_49_41','H_49_42','H_49_5','H_49_50','H_50','H_50_1','H_50_10','H_50_2','H_50_20','H_50_3','H_50_30','H_50_4','H_50_40','H_51','H_51_1','H_51_10','H_51_2','H_51_21','H_51_22','H_52','H_52_1','H_52_10','H_52_2','H_52_21','H_52_22','H_52_23','H_52_24','H_52_29','H_53','H_53_1','H_53_10','H_53_2','H_53_20','I','I_55','I_55_1','I_55_10','I_55_2','I_55_20','I_55_3','I_55_30','I_55_9','I_55_90','I_56','I_56_1','I_56_10','I_56_2','I_56_21','I_56_29','I_56_3','I_56_30','J','J_58','J_58_1','J_58_11','J_58_12','J_58_13','J_58_14','J_58_19','J_58_2','J_58_21','J_58_29','J_59','J_59_1','J_59_11','J_59_12','J_59_13','J_59_14','J_59_2','J_59_20','J_60','J_60_1','J_60_10','J_60_2','J_60_20','J_61','J_61_1','J_61_10','J_61_2','J_61_20','J_61_3','J_61_30','J_61_9','J_61_90','J_62','J_62_0','J_62_01','J_62_02','J_62_03','J_62_09','J_63','J_63_1','J_63_11','J_63_12','J_63_9','J_63_91','J_63_99','K','K_64','K_64_1','K_64_11','K_64_19','K_64_2','K_64_20','K_64_3','K_64_30','K_64_9','K_64_91','K_64_92','K_64_99','K_65','K_65_1','K_65_11','K_65_12','K_65_2','K_65_20','K_65_3','K_65_30','K_66','K_66_1','K_66_11','K_66_12','K_66_19','K_66_2','K_66_21','K_66_22','K_66_29','K_66_3','K_66_30','L','L_68','L_68_1','L_68_10','L_68_2','L_68_20','L_68_3','L_68_31','L_68_32','M','M_69','M_69_1','M_69_10','M_69_2','M_69_20','M_70','M_70_1','M_70_10','M_70_2','M_70_21','M_70_22','M_71','M_71_1','M_71_11','M_71_12','M_71_2','M_71_20','M_72','M_72_1','M_72_11','M_72_19','M_72_2','M_72_20','M_73','M_73_1','M_73_11','M_73_12','M_73_2','M_73_20','M_74','M_74_1','M_74_10','M_74_2','M_74_20','M_74_3','M_74_30','M_74_9','M_74_90','M_75','M_75_0','M_75_00','N','N_77','N_77_1','N_77_11','N_77_12','N_77_2','N_77_21','N_77_22','N_77_29','N_77_3','N_77_31','N_77_32','N_77_33','N_77_34','N_77_35','N_77_39','N_77_4','N_77_40','N_78','N_78_1','N_78_10','N_78_2','N_78_20','N_78_3','N_78_30','N_79','N_79_1','N_79_11','N_79_12','N_79_9','N_79_90','N_80','N_80_1','N_80_10','N_80_2','N_80_20','N_80_3','N_80_30','N_81','N_81_1','N_81_10','N_81_2','N_81_21','N_81_22','N_81_29','N_81_3','N_81_30','N_82','N_82_1','N_82_11','N_82_19','N_82_2','N_82_20','N_82_3','N_82_30','N_82_9','N_82_91','N_82_92','N_82_99','O','O_84','O_84_1','O_84_11','O_84_12','O_84_13','O_84_2','O_84_21','O_84_22','O_84_23','O_84_24','O_84_25','O_84_3','O_84_30','P','P_85','P_85_1','P_85_10','P_85_2','P_85_20','P_85_3','P_85_31','P_85_32','P_85_4','P_85_41','P_85_42','P_85_5','P_85_51','P_85_52','P_85_53','P_85_59','P_85_6','P_85_60','Q','Q_86','Q_86_1','Q_86_10','Q_86_2','Q_86_21','Q_86_22','Q_86_23','Q_86_9','Q_86_90','Q_87','Q_87_1','Q_87_10','Q_87_2','Q_87_20','Q_87_3','Q_87_30','Q_87_9','Q_87_90','Q_88','Q_88_1','Q_88_10','Q_88_9','Q_88_91','Q_88_99','R','R_90','R_90_0','R_90_01','R_90_02','R_90_03','R_90_04','R_91','R_91_0','R_91_01','R_91_02','R_91_03','R_91_04','R_92','R_92_0','R_92_00','R_93','R_93_1','R_93_11','R_93_12','R_93_13','R_93_19','R_93_2','R_93_21','R_93_29','S','S_94','S_94_1','S_94_11','S_94_12','S_94_2','S_94_20','S_94_9','S_94_91','S_94_92','S_94_99','S_95','S_95_1','S_95_11','S_95_12','S_95_2','S_95_21','S_95_22','S_95_23','S_95_24','S_95_25','S_95_29','S_96','S_96_0','S_96_01','S_96_02','S_96_03','S_96_04','S_96_09','T','T_97','T_97_0','T_97_00','T_98','T_98_1','T_98_10','T_98_2','T_98_20','U','U_99','U_99_0','U_99_00') DEFAULT NULL,
    other_identifier_number   VARCHAR(50)  DEFAULT NULL,
    pic                       VARCHAR(9)   DEFAULT NULL,
    CONSTRAINT fk_project_partner_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    UNIQUE KEY project_partner_project_name (project_id, abbreviation)
);

CREATE TABLE project_partner_transl
(
    source_entity_id             INT UNSIGNED NOT NULL,
    language                     VARCHAR(3) NOT NULL,
    department                   TEXT(255) DEFAULT NULL,
    other_identifier_description VARCHAR(127) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_partner_transl_to_partner FOREIGN KEY (source_entity_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_address
(
    partner_id   INT UNSIGNED                        NOT NULL,
    type         ENUM ('Organization', 'Department') NOT NULL,
    country      VARCHAR(100) DEFAULT NULL,
    nuts_region2 VARCHAR(100) DEFAULT NULL,
    nuts_region3 VARCHAR(100) DEFAULT NULL,
    street       VARCHAR(50)  DEFAULT NULL,
    house_number VARCHAR(20)  DEFAULT NULL,
    postal_code  VARCHAR(20)  DEFAULT NULL,
    city         VARCHAR(50)  DEFAULT NULL,
    homepage     VARCHAR(250) DEFAULT NULL,
    PRIMARY KEY (partner_id, type),
    CONSTRAINT fk_project_partner_address_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_address
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package
(
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id INT UNSIGNED NOT NULL,
    number     INT DEFAULT NULL,
    CONSTRAINT fk_project_work_package_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_transl
(
    source_entity_id       INT UNSIGNED NOT NULL,
    language               VARCHAR(3)   NOT NULL,
    name                   VARCHAR(100) DEFAULT NULL,
    specific_objective     VARCHAR(250) NULL,
    objective_and_audience VARCHAR(500) NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_work_package_transl_to_work_package FOREIGN KEY (source_entity_id) REFERENCES project_work_package (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_output
(
    work_package_id     INT UNSIGNED     NOT NULL,
    indicator_output_id INT UNSIGNED      DEFAULT NULL,
    period_number       SMALLINT UNSIGNED DEFAULT NULL,
    output_number       TINYINT UNSIGNED NOT NULL,
    target_value        DECIMAL(11, 2)    DEFAULT NULL,
    PRIMARY KEY (work_package_id, output_number),
    CONSTRAINT fk_project_work_package_output_to_project_work_package
        FOREIGN KEY (work_package_id)
            REFERENCES project_work_package (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_work_package_output_to_programme_indicator_output
        FOREIGN KEY (indicator_output_id) REFERENCES programme_indicator_output (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_output
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_output_transl
(
    work_package_id INT UNSIGNED     NOT NULL,
    output_number   TINYINT UNSIGNED NOT NULL,
    language        VARCHAR(3)       NOT NULL,
    title           VARCHAR(200) DEFAULT NULL,
    description     VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (work_package_id, output_number, language),
    CONSTRAINT fk_project_work_package_output_transl_to_project_work_pkg_out
        FOREIGN KEY (work_package_id, output_number)
            REFERENCES project_work_package_output (work_package_id, output_number)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_output_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity
(
    work_package_id INT UNSIGNED     NOT NULL,
    activity_number TINYINT UNSIGNED NOT NULL,
    start_period    SMALLINT UNSIGNED DEFAULT NULL,
    end_period      SMALLINT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (work_package_id, activity_number),
    CONSTRAINT fk_project_work_package_activity_to_project_work_package
        FOREIGN KEY (work_package_id)
            REFERENCES project_work_package (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_activity
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity_transl
(
    work_package_id INT UNSIGNED     NOT NULL,
    activity_number TINYINT UNSIGNED NOT NULL,
    language VARCHAR(3) NOT NULL,
    title           VARCHAR(200)      DEFAULT NULL,
    description     TEXT(500)         DEFAULT NULL,
    PRIMARY KEY (work_package_id, activity_number, language),
    CONSTRAINT fk_project_work_package_activity_transl_to_project_work_pkg_acti
        FOREIGN KEY (work_package_id, activity_number)
            REFERENCES project_work_package_activity (work_package_id, activity_number)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_activity_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity_deliverable
(
    work_package_id    INT UNSIGNED     NOT NULL,
    activity_number    TINYINT UNSIGNED NOT NULL,
    deliverable_number TINYINT UNSIGNED NOT NULL,
    start_period       SMALLINT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (work_package_id, activity_number, deliverable_number),
    CONSTRAINT fk_project_work_package_activity_d_to_project_work_package_activ
        FOREIGN KEY (work_package_id, activity_number)
            REFERENCES project_work_package_activity (work_package_id, activity_number)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_activity_deliverable
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity_deliverable_transl
(
    work_package_id    INT UNSIGNED     NOT NULL,
    activity_number    TINYINT UNSIGNED NOT NULL,
    deliverable_number TINYINT UNSIGNED NOT NULL,
    language VARCHAR(3) NOT NULL,
    description        TEXT(200)         DEFAULT NULL,
    PRIMARY KEY (work_package_id, activity_number, deliverable_number, language),
    CONSTRAINT fk_project_work_package_activity_del_transl_to_prjct_wrk_pckg_ad
        FOREIGN KEY (work_package_id, activity_number, deliverable_number)
            REFERENCES project_work_package_activity_deliverable (work_package_id, activity_number, deliverable_number)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_activity_deliverable_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity_partner
(
    work_package_id    INT UNSIGNED NOT NULL,
    activity_number    TINYINT UNSIGNED NOT NULL,
    project_partner_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (work_package_id, activity_number, project_partner_id),
    CONSTRAINT fk_project_wp_activity_partner_to_project_wp
        FOREIGN KEY (work_package_id, activity_number) REFERENCES project_work_package_activity (work_package_id, activity_number)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_partner_to_project_partner
        FOREIGN KEY (project_partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
);

ALTER TABLE project_work_package_activity_partner
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_investment
(
    id                                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    work_package_id                        INT UNSIGNED  NOT NULL,
    investment_number                      INT UNSIGNED  NOT NULL,
    country                                VARCHAR(100) DEFAULT NULL,
    nuts_region2                           VARCHAR(100) DEFAULT NULL,
    nuts_region3                           VARCHAR(100) DEFAULT NULL,
    street                                 VARCHAR(50)  DEFAULT NULL,
    house_number                           VARCHAR(20)  DEFAULT NULL,
    postal_code                            VARCHAR(20)  DEFAULT NULL,
    city                                   VARCHAR(50)  DEFAULT NULL,
    homepage                               VARCHAR(250) DEFAULT NULL,

    CONSTRAINT fk_project_work_package_investment_work_package FOREIGN KEY (work_package_id) REFERENCES project_work_package (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_investment
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_investment_transl
(
    investment_id                         INT UNSIGNED NOT NULL,
    language                              VARCHAR(3)   NOT NULL,
    title                                 VARCHAR(50),
    justification_explanation             VARCHAR(2000),
    justification_transactional_relevance VARCHAR(2000),
    justification_benefits                VARCHAR(2000),
    justification_pilot                   VARCHAR(2000),
    risk                                  VARCHAR(2000),
    documentation                         VARCHAR(2000),
    ownership_site_location               VARCHAR(500),
    ownership_retain                      VARCHAR(500),
    ownership_maintenance                 VARCHAR(2000),
    PRIMARY KEY (investment_id, language),
    CONSTRAINT fk_project_work_package_invest_transl_to_project_work_pkg_invest
        FOREIGN KEY (investment_id)
            REFERENCES project_work_package_investment (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_work_package_investment_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c1_overall_objective
(
    project_id INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_description_c1_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c1_overall_objective
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c1_overall_objective_transl
(
    project_id        INT UNSIGNED NOT NULL,
    language          VARCHAR(3)   NOT NULL,
    overall_objective TEXT(500) DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_description_c1_overall_objective_transl FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c1_overall_objective_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance
(
    project_id          INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_description_c2_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance_transl
(
    project_id                INT UNSIGNED NOT NULL,
    language                  VARCHAR(3)   NOT NULL,
    territorial_challenge     TEXT(5000) DEFAULT NULL,
    common_challenge          TEXT(5000) DEFAULT NULL,
    transnational_cooperation TEXT(5000) DEFAULT NULL,
    available_knowledge       TEXT(5000) DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_description_c2_relevance_transl_to_project_c2 FOREIGN KEY (project_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance_benefit
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT UNSIGNED           NOT NULL,
    target_group         VARCHAR(127)           NOT NULL,
    CONSTRAINT fk_project_benefit_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_benefit
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance_benefit_transl
(
    reference_id  BINARY(16) NOT NULL,
    language      VARCHAR(3) NOT NULL,
    specification TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (reference_id, language),
    CONSTRAINT fk_project_description_c2_relevance_benefit_transl FOREIGN KEY (reference_id)
        REFERENCES project_description_c2_relevance_benefit (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_benefit_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance_strategy
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT UNSIGNED           NOT NULL,
    strategy             VARCHAR(127) DEFAULT NULL,
    CONSTRAINT fk_project_strategy_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_strategy_to_project_call_strategy FOREIGN KEY (strategy) REFERENCES programme_strategy (strategy)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_strategy
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance_strategy_transl
(
    reference_id                BINARY(16) NOT NULL,
    language                    VARCHAR(3) NOT NULL,
    specification               TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (reference_id, language),
    CONSTRAINT fk_project_description_c2_relevance_strategy_transl FOREIGN KEY (reference_id)
        REFERENCES project_description_c2_relevance_strategy (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_strategy_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance_synergy
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT UNSIGNED           NOT NULL,
    CONSTRAINT fk_project_synergy_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_synergy
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c2_relevance_synergy_transl
(
    reference_id  BINARY(16) NOT NULL,
    language      VARCHAR(3) NOT NULL,
    synergy       TEXT(2000) DEFAULT NULL,
    specification TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (reference_id, language),
    CONSTRAINT fk_project_description_c2_relevance_synergy_transl FOREIGN KEY (reference_id)
        REFERENCES project_description_c2_relevance_synergy (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c2_relevance_synergy_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c3_partnership
(
    project_id INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_description_c3_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c3_partnership
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c3_partnership_transl
(
    project_id          INT UNSIGNED NOT NULL,
    language            VARCHAR(3)   NOT NULL,
    project_partnership TEXT(500) DEFAULT NULL,
    PRIMARY KEY (project_id, language),
    CONSTRAINT fk_project_description_c3_partnership_transl FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c3_partnership_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c7_management
(
    project_id                              INT UNSIGNED PRIMARY KEY,
    project_joint_development               BOOLEAN                                                DEFAULT FALSE,
    project_joint_implementation            BOOLEAN                                                DEFAULT FALSE,
    project_joint_staffing                  BOOLEAN                                                DEFAULT FALSE,
    project_joint_financing                 BOOLEAN                                                DEFAULT FALSE,
    sustainable_development_criteria_effect ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,
    equal_opportunities_effect              ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,
    sexual_equality_effect                  ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,
    CONSTRAINT fk_project_management_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c7_management
    ADD SYSTEM VERSIONING;

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

ALTER TABLE project_description_c7_management_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_description_c8_long_term_plans
(
    project_id              INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_long_term_plans_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_description_c8_long_term_plans
    ADD SYSTEM VERSIONING;

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

ALTER TABLE project_description_c8_long_term_plans_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_contact
(
    partner_id INT UNSIGNED                                  NOT NULL,
    type       ENUM ('LegalRepresentative', 'ContactPerson') NOT NULL,
    title      VARCHAR(25)  DEFAULT NULL,
    first_name VARCHAR(50)  DEFAULT NULL,
    last_name  VARCHAR(50)  DEFAULT NULL,
    email      VARCHAR(255) DEFAULT NULL,
    telephone  VARCHAR(25)  DEFAULT NULL,
    PRIMARY KEY (partner_id, type),
    CONSTRAINT fk_project_partner_contact_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_contact
    ADD SYSTEM VERSIONING;

CREATE TABLE project_call_fund
(
    programme_fund INT UNSIGNED NOT NULL,
    call_id        INT UNSIGNED NOT NULL,
    CONSTRAINT fk_project_call_fund_to_programme_fund
        FOREIGN KEY (programme_fund)
            REFERENCES programme_fund (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_fund_to_call
        FOREIGN KEY (call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT pk_project_call_fund PRIMARY KEY (programme_fund, call_id)
);

CREATE TABLE project_partner_motivation
(
    partner_id              INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_partner_motivation_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_motivation
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_motivation_transl
(
    partner_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    organization_relevance  TEXT DEFAULT NULL,
    organization_role       TEXT DEFAULT NULL,
    organization_experience TEXT DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_motivation_transl_to_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_motivation_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_state_aid
(
    partner_id INT UNSIGNED PRIMARY KEY,
    answer1    BOOLEAN DEFAULT NULL,
    answer2    BOOLEAN DEFAULT NULL,
    answer3    BOOLEAN DEFAULT NULL,
    answer4    BOOLEAN DEFAULT NULL,
    CONSTRAINT fk_project_partner_state_aid_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_state_aid
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_state_aid_transl
(
    partner_id     INT UNSIGNED NOT NULL,
    language       VARCHAR(3) NOT NULL,
    justification1 TEXT DEFAULT NULL,
    justification2 TEXT DEFAULT NULL,
    justification3 TEXT DEFAULT NULL,
    justification4 TEXT DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_state_aid_transl_to_partner_state_aid FOREIGN KEY (partner_id) REFERENCES project_partner_state_aid (partner_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_state_aid_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_contribution
(
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    partner_id INT UNSIGNED   NOT NULL,
    name       VARCHAR(255)   DEFAULT NULL,
    status     ENUM ('Private', 'Public', 'AutomaticPublic') DEFAULT NULL,
    amount     DECIMAL(15, 2) NOT NULL,
    CONSTRAINT fk_project_partner_contribution_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_contribution
    ADD SYSTEM VERSIONING;

CREATE TABLE programme_legal_status
(
    id   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    type ENUM ('Public', 'Private', 'Other') NOT NULL DEFAULT 'Other'
);

CREATE TABLE programme_legal_status_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    description      VARCHAR(127) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_programme_legal_status_transl_to_programme_legal_status
        FOREIGN KEY (source_entity_id) REFERENCES programme_legal_status (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT INTO programme_legal_status (type) VALUE ('Public');
SELECT id INTO @id FROM programme_legal_status ORDER BY id DESC LIMIT 1;
INSERT INTO programme_legal_status_transl(source_entity_id, language, description)
    VALUE (@id, 'EN', 'Public');

INSERT INTO programme_legal_status (type) VALUE ('Private');
SELECT id INTO @id FROM programme_legal_status ORDER BY id DESC LIMIT 1;
INSERT INTO programme_legal_status_transl(source_entity_id, language, description)
    VALUE (@id, 'EN', 'Private');

ALTER TABLE project_partner
    ADD COLUMN legal_status_id INT UNSIGNED NOT NULL AFTER partner_type,
    ADD CONSTRAINT fk_project_partner_to_programme_legal_status
        FOREIGN KEY (legal_status_id)
            REFERENCES programme_legal_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

ALTER TABLE project_partner
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_staff_cost
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    row_sum         DECIMAL(17, 2) UNSIGNED NOT NULL,
    unit_cost_id    INT UNSIGNED                     DEFAULT NULL,
    CONSTRAINT fk_project_partner_budget_staff_cost_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_staff_cost
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_staff_cost_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    description      TEXT(255) DEFAULT NULL,
    comment          TEXT(255) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_partner_budget_staff_cost_transl_to_project FOREIGN KEY (source_entity_id) REFERENCES project_partner_budget_staff_cost (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_staff_cost_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_travel
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    row_sum         DECIMAL(17, 2) UNSIGNED NOT NULL,
    unit_cost_id    INT UNSIGNED                     DEFAULT NULL,
    CONSTRAINT fk_project_partner_budget_travel_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_travel
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_travel_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_partner_budget_travel_transl_to_project FOREIGN KEY (source_entity_id) REFERENCES project_partner_budget_travel (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_travel_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_external
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    row_sum         DECIMAL(17, 2) UNSIGNED NOT NULL,
    investment_id   INT UNSIGNED                     DEFAULT NULL,
    unit_cost_id    INT UNSIGNED                     DEFAULT NULL,
    CONSTRAINT fk_project_partner_budget_external_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_external
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_external_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    award_procedures TEXT(250) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_partner_budget_external_transl_to_project FOREIGN KEY (source_entity_id) REFERENCES project_partner_budget_external (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_external_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_equipment
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    row_sum         DECIMAL(17, 2) UNSIGNED NOT NULL,
    investment_id   INT UNSIGNED                     DEFAULT NULL,
    unit_cost_id    INT UNSIGNED                     DEFAULT NULL,
    CONSTRAINT fk_project_partner_budget_equipment_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_equipment
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_equipment_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    award_procedures TEXT(250) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_partner_budget_equipment_transl_to_project FOREIGN KEY (source_entity_id) REFERENCES project_partner_budget_equipment (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_equipment_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_infrastructure
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    row_sum         DECIMAL(17, 2) UNSIGNED NOT NULL,
    investment_id   INT UNSIGNED                     DEFAULT NULL,
    unit_cost_id    INT UNSIGNED                     DEFAULT NULL,
    CONSTRAINT fk_project_partner_budget_infrastructure_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_infrastructure
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_infrastructure_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    award_procedures TEXT(250) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_partner_budget_infrastructure_transl_to_project FOREIGN KEY (source_entity_id) REFERENCES project_partner_budget_infrastructure (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_infrastructure_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_co_financing
(
    partner_id        INT UNSIGNED NOT NULL,
    order_nr          TINYINT UNSIGNED NOT NULL,
    percentage        DECIMAL(11, 2) NOT NULL,
    programme_fund_id INT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (partner_id, order_nr),
    CONSTRAINT fk_project_partner_co_financing_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_co_financing_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_co_financing
    ADD SYSTEM VERSIONING;

CREATE TABLE programme_language
(
    code     VARCHAR(3) NOT NULL PRIMARY KEY,
    ui       BOOLEAN    NOT NULL DEFAULT FALSE,
    fallback BOOLEAN    NOT NULL DEFAULT FALSE,
    input    BOOLEAN    NOT NULL DEFAULT FALSE
);

INSERT INTO programme_language (code, ui, fallback, input)
VALUES ('BE', false, false, false),
       ('BG', false, false, false),
       ('CS', false, false, false),
       ('DA', false, false, false),
       ('DE', false, false, false),
       ('EL', false, false, false),
       ('EN', true, true, true),
       ('ES', false, false, false),
       ('ET', false, false, false),
       ('FI', false, false, false),
       ('FR', false, false, false),
       ('GA', false, false, false),
       ('HR', false, false, false),
       ('HU', false, false, false),
       ('IT', false, false, false),
       ('LB', false, false, false),
       ('LT', false, false, false),
       ('LV', false, false, false),
       ('MT', false, false, false),
       ('MK', false, false, false),
       ('NL', false, false, false),
       ('NO', false, false, false),
       ('PL', false, false, false),
       ('PT', false, false, false),
       ('RO', false, false, false),
       ('RU', false, false, false),
       ('SK', false, false, false),
       ('SL', false, false, false),
       ('SQ', false, false, false),
       ('SR', false, false, false),
       ('SV', false, false, false),
       ('TR', false, false, false),
       ('UK', false, false, false);

CREATE TABLE project_associated_organization
(
    id                        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id                INT UNSIGNED NOT NULL,
    partner_id                INT UNSIGNED NOT NULL,
    name_in_original_language VARCHAR(127) NOT NULL,
    name_in_english           VARCHAR(127) NOT NULL,
    sort_number               INT        DEFAULT NULL,
    CONSTRAINT fk_project_associated_organization_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_associated_organization_to_partner
        FOREIGN KEY (partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_associated_organization
    ADD SYSTEM VERSIONING;

CREATE TABLE project_associated_organization_transl
(
    organization_id  INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    role_description TEXT DEFAULT NULL,
    PRIMARY KEY (organization_id, language),
    CONSTRAINT fk_project_associated_organization_transl_to_organization FOREIGN KEY (organization_id) REFERENCES project_associated_organization (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_associated_organization_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_associated_organization_contact
(
    organization_id INT UNSIGNED                                  NOT NULL,
    type            ENUM ('LegalRepresentative', 'ContactPerson') NOT NULL,
    title           VARCHAR(25)  DEFAULT NULL,
    first_name      VARCHAR(50)  DEFAULT NULL,
    last_name       VARCHAR(50)  DEFAULT NULL,
    email           VARCHAR(255) DEFAULT NULL,
    telephone       VARCHAR(25)  DEFAULT NULL,
    PRIMARY KEY (organization_id, type),
    CONSTRAINT fk_project_as_org_contact_to_project_associated_organization FOREIGN KEY (organization_id) REFERENCES project_associated_organization (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_associated_organization_contact
    ADD SYSTEM VERSIONING;

CREATE TABLE project_associated_organization_address
(
    organization_id INT UNSIGNED PRIMARY KEY,
    country         VARCHAR(100) DEFAULT NULL,
    nuts_region2    VARCHAR(100) DEFAULT NULL,
    nuts_region3    VARCHAR(100) DEFAULT NULL,
    street          VARCHAR(50)  DEFAULT NULL,
    house_number    VARCHAR(20)  DEFAULT NULL,
    postal_code     VARCHAR(20)  DEFAULT NULL,
    city            VARCHAR(50)  DEFAULT NULL,
    homepage        VARCHAR(250) DEFAULT NULL,
    CONSTRAINT fk_project_as_org_address_to_project_associated_organization FOREIGN KEY (organization_id) REFERENCES project_associated_organization (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_associated_organization_address
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_options
(
    partner_id                                          INT UNSIGNED PRIMARY KEY,
    staff_costs_flat_rate                               TINYINT UNSIGNED DEFAULT NULL,
    office_and_administration_on_staff_costs_flat_rate  TINYINT UNSIGNED DEFAULT NULL,
    travel_and_accommodation_on_staff_costs_flat_rate   TINYINT UNSIGNED DEFAULT NULL,
    other_costs_on_staff_costs_flat_rate                TINYINT UNSIGNED DEFAULT NULL,
    office_and_administration_on_direct_costs_flat_rate TINYINT UNSIGNED DEFAULT NULL,
    CONSTRAINT fk_project_partner_budget_options_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_options
    ADD SYSTEM VERSIONING;

CREATE TABLE project_period
(
    project_id INT UNSIGNED      NOT NULL,
    number     SMALLINT UNSIGNED NOT NULL,
    start      SMALLINT UNSIGNED NOT NULL,
    end        SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY (project_id, number),
    CONSTRAINT fk_project_period_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_period
    ADD SYSTEM VERSIONING;

CREATE TABLE project_call_flat_rate
(
    call_id       INT UNSIGNED                                                                        NOT NULL,
    type          ENUM ('StaffCost','OfficeOnStaff','OfficeOnOther', 'TravelOnStaff', 'OtherOnStaff') NOT NULL,
    rate          TINYINT UNSIGNED                                                                    NOT NULL,
    is_adjustable BOOLEAN                                                                             NOT NULL,
    CONSTRAINT pk_project_call_flat_rate PRIMARY KEY (call_id, type),
    CONSTRAINT fk_project_call_flat_rate_to_project_call FOREIGN KEY (call_id) REFERENCES project_call (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE programme_lump_sum
(
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    cost              DECIMAL(11, 2)                                   NOT NULL,
    splitting_allowed BOOLEAN                                          NOT NULL,
    phase             ENUM ('Preparation', 'Implementation','Closure') NOT NULL
);

CREATE TABLE programme_lump_sum_transl
(
    programme_lump_sum_id   INT UNSIGNED    NOT NULL,
    language                VARCHAR(3)      NOT NULL,
    name                    TEXT(50)        DEFAULT NULL,
    description             TEXT(500)       DEFAULT NULL,
    PRIMARY KEY (programme_lump_sum_id, language),
    CONSTRAINT fk_programme_lump_sum_transl_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id)
            REFERENCES programme_lump_sum (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE programme_lump_sum_budget_category
(
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    programme_lump_sum_id INT UNSIGNED NOT NULL,
    category              ENUM (
        'StaffCosts',
        'OfficeAndAdministrationCosts',
        'TravelAndAccommodationCosts',
        'ExternalCosts',
        'EquipmentCosts',
        'InfrastructureCosts' )        NOT NULL,
    CONSTRAINT fk_programme_lump_sum_budget_category_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id) REFERENCES programme_lump_sum (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE programme_unit_cost
(
    id                   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    is_one_cost_category BOOLEAN        NOT NULL DEFAULT FALSE,
    cost_per_unit        DECIMAL(11, 2) NOT NULL
);

CREATE TABLE programme_unit_cost_transl
(
    programme_unit_cost_id  INT UNSIGNED    NOT NULL,
    language                VARCHAR(3)      NOT NULL,
    name                    TEXT(50)        DEFAULT NULL,
    description             TEXT(500)       DEFAULT NULL,
    type                    TEXT(25)        DEFAULT NULL,
    PRIMARY KEY (programme_unit_cost_id, language),
    CONSTRAINT fk_programme_unit_cost_transl_to_programme_unit_cost
        FOREIGN KEY (programme_unit_cost_id)
            REFERENCES programme_unit_cost (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
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

CREATE TABLE project_call_lump_sum
(
    project_call_id       INT UNSIGNED NOT NULL,
    programme_lump_sum_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (project_call_id, programme_lump_sum_id),
    CONSTRAINT fk_project_call_lump_sum_to_project_call
        FOREIGN KEY (project_call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_lump_sum_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id)
            REFERENCES programme_lump_sum (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

CREATE TABLE project_call_unit_cost
(
    project_call_id        INT UNSIGNED NOT NULL,
    programme_unit_cost_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (project_call_id, programme_unit_cost_id),
    CONSTRAINT fk_project_call_unit_cost_to_project_call
        FOREIGN KEY (project_call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_unit_cost_to_programme_unit_cost
        FOREIGN KEY (programme_unit_cost_id)
            REFERENCES programme_unit_cost (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

CREATE TABLE project_result
(
    project_id          INT UNSIGNED     NOT NULL,
    result_number       TINYINT UNSIGNED NOT NULL,
    period_number       SMALLINT UNSIGNED,
    indicator_result_id INT UNSIGNED              DEFAULT NULL,
    baseline            DECIMAL(11, 2)   NOT NULL DEFAULT 0,
    target_value        DECIMAL(11, 2)            DEFAULT NULL,
    PRIMARY KEY (project_id, result_number),
    CONSTRAINT fk_project_result_to_project
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_result_indicator_to_programme_indicator_result
        FOREIGN KEY (indicator_result_id) REFERENCES programme_indicator_result (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

ALTER TABLE project_result
    ADD SYSTEM VERSIONING;

CREATE TABLE project_result_transl
(
    project_id    INT UNSIGNED     NOT NULL,
    result_number TINYINT UNSIGNED NOT NULL,
    language      VARCHAR(3)       NOT NULL,
    description   TEXT(500) DEFAULT NULL,
    PRIMARY KEY (project_id, result_number, language),
    CONSTRAINT fk_project_result_transl_to_project_result FOREIGN KEY (project_id, result_number) REFERENCES project_result (project_id, result_number)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_result_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_unit_cost
(
    id                      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id              INT UNSIGNED NOT NULL,
    programme_unit_cost_id  INT UNSIGNED NOT NULL,
    number_of_units         DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    row_sum                 DECIMAL(17, 2) UNSIGNED NOT NULL,
    CONSTRAINT fk_project_partner_budget_unit_cost_to_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_budget_unit_cost_to_programme_unit_cost
        FOREIGN KEY (programme_unit_cost_id) REFERENCES programme_unit_cost (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_unit_cost
    ADD SYSTEM VERSIONING;

CREATE TABLE project_lump_sum
(
    project_id            INT UNSIGNED     NOT NULL,
    order_nr              TINYINT UNSIGNED NOT NULL,
    programme_lump_sum_id INT UNSIGNED     NOT NULL,
    end_period            TINYINT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (project_id, order_nr),
    CONSTRAINT fk_project_lump_sum_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_lump_sum_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id) REFERENCES programme_lump_sum (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

ALTER TABLE project_lump_sum
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_lump_sum
(
    project_id INT UNSIGNED     NOT NULL,
    order_nr   TINYINT UNSIGNED NOT NULL,
    project_partner_id  INT UNSIGNED     NOT NULL,
    amount              DECIMAL(11, 2)   NOT NULL,
    PRIMARY KEY (project_id, order_nr, project_partner_id),
    CONSTRAINT fk_project_partner_lump_sum_to_project_lump_sum
        FOREIGN KEY (project_id, order_nr) REFERENCES project_lump_sum (project_id, order_nr)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_lump_sum_to_project_partner
        FOREIGN KEY (project_partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE project_partner_lump_sum
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_equipment_period
(
    budget_id     INT UNSIGNED            NOT NULL,
    project_id    INT UNSIGNED            NOT NULL,
    period_number SMALLINT UNSIGNED       NOT NULL,
    amount        DECIMAL(17, 2) UNSIGNED NOT NULL,
    PRIMARY KEY (budget_id, project_id, period_number),
    CONSTRAINT fk_project_period_equipment_cost FOREIGN KEY (project_id, period_number) REFERENCES project_period (project_id, number)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_budget_equipment FOREIGN KEY (budget_id) REFERENCES project_partner_budget_equipment (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_equipment_period
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_external_period
(
    budget_id     INT UNSIGNED            NOT NULL,
    project_id    INT UNSIGNED            NOT NULL,
    period_number SMALLINT UNSIGNED       NOT NULL,
    amount        DECIMAL(17, 2) UNSIGNED NOT NULL,
    PRIMARY KEY (budget_id, project_id, period_number),
    CONSTRAINT fk_project_period_external_cost FOREIGN KEY (project_id, period_number) REFERENCES project_period (project_id, number) ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_budget_external FOREIGN KEY (budget_id) REFERENCES project_partner_budget_external (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_external_period
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_infrastructure_period
(
    budget_id     INT UNSIGNED            NOT NULL,
    project_id    INT UNSIGNED            NOT NULL,
    period_number SMALLINT UNSIGNED       NOT NULL,
    amount        DECIMAL(17, 2) UNSIGNED NOT NULL,
    PRIMARY KEY (budget_id, project_id, period_number),
    CONSTRAINT fk_project_period_infrastructure_cost FOREIGN KEY (project_id, period_number) REFERENCES project_period (project_id, number) ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_budget_infrastructure FOREIGN KEY (budget_id) REFERENCES project_partner_budget_infrastructure (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_infrastructure_period
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_staff_cost_period
(
    budget_id     INT UNSIGNED            NOT NULL,
    project_id    INT UNSIGNED            NOT NULL,
    period_number SMALLINT UNSIGNED       NOT NULL,
    amount        DECIMAL(17, 2) UNSIGNED NOT NULL,
    PRIMARY KEY (budget_id, project_id, period_number),
    CONSTRAINT fk_project_period_staff_cost FOREIGN KEY (project_id, period_number) REFERENCES project_period (project_id, number) ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_budget_staff_cost FOREIGN KEY (budget_id) REFERENCES project_partner_budget_staff_cost (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_staff_cost_period
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_travel_period
(
    budget_id     INT UNSIGNED            NOT NULL,
    project_id    INT UNSIGNED            NOT NULL,
    period_number SMALLINT UNSIGNED       NOT NULL,
    amount        DECIMAL(17, 2) UNSIGNED NOT NULL,
    PRIMARY KEY (budget_id, project_id, period_number),
    CONSTRAINT fk_project_period_travel_cost FOREIGN KEY (project_id, period_number) REFERENCES project_period (project_id, number)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_budget_travel FOREIGN KEY (budget_id) REFERENCES project_partner_budget_travel (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_travel_period
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_unit_cost_period
(
    budget_id     INT UNSIGNED            NOT NULL,
    project_id    INT UNSIGNED            NOT NULL,
    period_number SMALLINT UNSIGNED       NOT NULL,
    amount        DECIMAL(17, 2) UNSIGNED NOT NULL,
    PRIMARY KEY (budget_id, project_id, period_number),
    CONSTRAINT fk_project_period_unit_cost FOREIGN KEY (project_id, period_number) REFERENCES project_period (project_id, number)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_budget_unit_cost FOREIGN KEY (budget_id) REFERENCES project_partner_budget_unit_cost (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_unit_cost_period
    ADD SYSTEM VERSIONING;


-- TODO remove this table (used only for development purposes)
CREATE TABLE plugin_status
(
    plugin_key    VARCHAR(255) PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE application_form_field_configuration
(
    id                VARCHAR(255) NOT NULL,
    call_id           INT UNSIGNED NOT NULL,
    visibility_status ENUM ('NONE','STEP_ONE_AND_TWO','STEP_TWO_ONLY') NOT NULL DEFAULT 'STEP_ONE_AND_TWO',
    PRIMARY KEY (id, call_id),
    CONSTRAINT fk_application_form_field_configuration_to_call
        FOREIGN KEY (call_id) REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE translation_file
(
    file_type     ENUM ('Application', 'System'),
    language      VARCHAR(3)  NOT NULL,
    last_modified DATETIME(3) NOT NULL DEFAULT current_timestamp(3),
    PRIMARY KEY (file_type, language)
);

CREATE TABLE programme_state_aid
(
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    measure       ENUM ( 'General de minimis',
        'Road freight de minimis',
        'Agricultural de minimis',
        'Fishery and aquaculture sector de minimis',
        'SGEI de minimis',
        'GBER Article 14',
        'GBER Article 15',
        'GBER Article 16',
        'GBER Article 17',
        'GBER Article 18',
        'GBER Article 19',
        'GBER Article 19a',
        'GBER Article 19b',
        'GBER Article 20',
        'GBER Article 20a',
        'GBER Article 21',
        'GBER Article 22',
        'GBER Article 23',
        'GBER Article 24',
        'GBER Article 25 par. (a)',
        'GBER Article 25 par. (b)',
        'GBER Article 25 par. (c)',
        'GBER Article 25 par. (d)',
        'GBER Article 25a',
        'GBER Article 25b',
        'GBER Article 25c',
        'GBER Article 25d',
        'GBER Article 26',
        'GBER Article 27',
        'GBER Article 28',
        'GBER Article 29',
        'GBER Article 30',
        'GBER Article 31',
        'GBER Article 32',
        'GBER Article 33',
        'GBER Article 34',
        'GBER Article 35',
        'GBER Article 36',
        'GBER Article 36a',
        'GBER Article 37',
        'GBER Article 38',
        'GBER Article 39',
        'GBER Article 40',
        'GBER Article 41',
        'GBER Article 42',
        'GBER Article 43',
        'GBER Article 44',
        'GBER Article 45',
        'GBER Article 46',
        'GBER Article 47',
        'GBER Article 48',
        'GBER Article 49',
        'GBER Article 50',
        'GBER Article 51',
        'GBER Article 52',
        'GBER Article 52a',
        'GBER Article 52b',
        'GBER Article 52c',
        'GBER Article 53',
        'GBER Article 54',
        'GBER Article 55',
        'GBER Article 56',
        'GBER Article 56a',
        'GBER Article 56b',
        'GBER Article 56c',
        'GBER Article 56e',
        'GBER Article 56f',
        'Indirect aid',
        'RDI Framework',
        'SGEI Framework',
        'Other 1',
        'Other 2',
        'Other 3' ) NOT NULL DEFAULT 'Other 1',
    scheme_number VARCHAR(25) DEFAULT NULL,
    max_intensity DECIMAL(5, 2) UNSIGNED DEFAULT NULL,
    threshold DECIMAL(17, 2) UNSIGNED DEFAULT NULL
);

CREATE TABLE programme_state_aid_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    name             VARCHAR(250) DEFAULT NULL,
    abbreviated_name VARCHAR(50)  DEFAULT NULL,
    comments         VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_programme_state_aid_transl_to_programme_state_aid
        FOREIGN KEY (source_entity_id)
            REFERENCES programme_state_aid (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_call_state_aid
(
    programme_state_aid INT UNSIGNED NOT NULL,
    project_call_id        INT UNSIGNED NOT NULL,
    CONSTRAINT fk_project_call_state_aid_to_programme_state_aid
        FOREIGN KEY (programme_state_aid)
            REFERENCES programme_state_aid (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_state_aid_to_call
        FOREIGN KEY (project_call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT pk_project_call_state_aid PRIMARY KEY (programme_state_aid, project_call_id)
);
