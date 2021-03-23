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

INSERT INTO account_role (id, name)
VALUES (1, 'administrator'),
       (2, 'programme user'),
       (3, 'applicant user');

INSERT INTO account (email, name, surname, account_role_id, password)
VALUES ('admin@jems.eu', 'Admin', 'Admin', 1, '{bcrypt}$2a$10$U7oTeVv4GXWmZzL0lE1H8eX4.TpJyhwz6SlKefFKnb3VDLoivO8sC');

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
    id                                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
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
    programme_amending_decision_date   DATE
);

INSERT INTO programme_data (id)
VALUES (1);

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
        'OCTP Greenland',
        'OCTP',
        'Interreg Funds',
        'Other')     NOT NULL DEFAULT 'Other'
);

CREATE TABLE programme_fund_transl
(
    fund_id      INT UNSIGNED NOT NULL,
    language     VARCHAR(3)   NOT NULL,
    abbreviation VARCHAR(127) DEFAULT NULL,
    description  VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (fund_id, language),
    CONSTRAINT fk_programme_fund_transl_to_programme_fund
        FOREIGN KEY (fund_id) REFERENCES programme_fund (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

INSERT INTO programme_fund (type) VALUE ('ERDF');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'ERDF', 'Territorial cooperation Goal (Interreg)');

INSERT INTO programme_fund (type) VALUE ('IPA III CBC');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'IPA III CBC', 'Interreg A, external cross-border cooperation');

INSERT INTO programme_fund (type) VALUE ('Neighbourhood CBC');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'Neighbourhood CBC', 'Interreg A, external cross-border cooperation');

INSERT INTO programme_fund (type) VALUE ('IPA III');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'IPA III', 'Interreg B and C');

INSERT INTO programme_fund (type) VALUE ('NDICI');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'NDICI', 'Interreg B and C');

INSERT INTO programme_fund (type) VALUE ('OCTP Greenland');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'OCTP Greenland', 'Interreg B and C');

INSERT INTO programme_fund (type) VALUE ('OCTP');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation, description)
    VALUE (@id, 'EN', 'OCTP', 'Interreg C and D');

INSERT INTO programme_fund (type) VALUE ('Interreg Funds');
SELECT id INTO @id FROM programme_fund ORDER BY id DESC LIMIT 1;
INSERT INTO programme_fund_transl(fund_id, language, abbreviation, description)
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
       ('AtlanticStrategy');

CREATE TABLE project_call
(
    id                         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    creator_id                 INT UNSIGNED     NOT NULL,
    name                       VARCHAR(255)     NOT NULL UNIQUE,
    status                     VARCHAR(127)     NOT NULL,
    start_date                 DATETIME(3)      NOT NULL,
    end_date                   DATETIME(3)      NOT NULL,
    length_of_period           TINYINT UNSIGNED NOT NULL,
    is_additional_fund_allowed BOOLEAN          NOT NULL DEFAULT FALSE,
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
    ADD COLUMN project_status_id       INT UNSIGNED NOT NULL AFTER applicant_id,
    ADD COLUMN first_submission_id     INT UNSIGNED DEFAULT NULL AFTER project_status_id,
    ADD COLUMN last_resubmission_id    INT UNSIGNED DEFAULT NULL AFTER first_submission_id,
    ADD COLUMN eligibility_decision_id INT UNSIGNED DEFAULT NULL AFTER last_resubmission_id,
    ADD COLUMN funding_decision_id     INT UNSIGNED DEFAULT NULL AFTER eligibility_decision_id,
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
    ADD CONSTRAINT fk_project_eligibility_decision_project_status
        FOREIGN KEY (eligibility_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    ADD CONSTRAINT fk_project_funding_decision_project_status
        FOREIGN KEY (funding_decision_id) REFERENCES project_status (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;

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

CREATE TABLE project_eligibility_assessment
(
    project_id INT UNSIGNED PRIMARY KEY,
    result     VARCHAR(127)                             NOT NULL,
    account_id INT UNSIGNED                             NOT NULL,
    updated    DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    note       VARCHAR(1000)                            NULL,
    CONSTRAINT fk_project_eligibility_assessment_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_eligibility_assessment_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_file
(
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    bucket      VARCHAR(100)                              NOT NULL,
    identifier  VARCHAR(255)                              NOT NULL,
    name        VARCHAR(255)                              NOT NULL,
    project_id  INT UNSIGNED                              NOT NULL,
    author_id   INT UNSIGNED                              NOT NULL,
    type        VARCHAR(127)                              NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    size        BIGINT                                    NOT NULL,
    updated     DATETIME(3)  DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    CONSTRAINT fk_project_file_to_account
        FOREIGN KEY (author_id) REFERENCES account (id),
    CONSTRAINT fk_project_file_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    UNIQUE KEY project_file_bucket_identifier (bucket, identifier)
);

CREATE TABLE project_partner
(
    id                        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_id                INT UNSIGNED NOT NULL,
    abbreviation              VARCHAR(15)  NOT NULL,
    role                      VARCHAR(127) NOT NULL,
    sort_number               INT                          DEFAULT NULL,
    name_in_original_language VARCHAR(127)                 DEFAULT NULL,
    name_in_english           VARCHAR(127)                 DEFAULT NULL,
    partner_type              VARCHAR(127)                 DEFAULT NULL,
    vat                       VARCHAR(50)                  DEFAULT NULL,
    vat_recovery              ENUM ('Yes', 'No', 'Partly') DEFAULT NULL,
    CONSTRAINT fk_project_partner_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    UNIQUE KEY project_partner_project_name (project_id, abbreviation)
);

CREATE TABLE project_partner_transl
(
    partner_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    department              TEXT(255) DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_transl_to_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_quality_assessment
(
    project_id INT UNSIGNED PRIMARY KEY,
    result     VARCHAR(127)  NOT NULL,
    account_id INT UNSIGNED  NOT NULL,
    updated    DATETIME(3)   NOT NULL DEFAULT current_timestamp(3),
    note       VARCHAR(1000) NULL,
    CONSTRAINT fk_project_quality_assessment_to_account
        FOREIGN KEY (account_id) REFERENCES account (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_quality_assessment_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

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

CREATE TABLE project_work_package_transl
(
    work_package_id         INT UNSIGNED NOT NULL,
    language               VARCHAR(3)   NOT NULL,
    name                   VARCHAR(100) DEFAULT NULL,
    specific_objective     VARCHAR(250) NULL,
    objective_and_audience VARCHAR(500) NULL,
    PRIMARY KEY (work_package_id, language),
    CONSTRAINT fk_work_package_transl_to_work_package FOREIGN KEY (work_package_id) REFERENCES project_work_package (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_description_c1_overall_objective
(
    project_id INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_description_c1_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_description_c2_relevance
(
    project_id          INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_description_c2_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_description_c2_relevance_benefit
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT UNSIGNED           NOT NULL,
    target_group         VARCHAR(127)           NOT NULL,
    CONSTRAINT fk_project_benefit_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_description_c2_relevance_synergy
(
    id                   BINARY(16) PRIMARY KEY NOT NULL, # UUID
    project_relevance_id INT UNSIGNED           NOT NULL,
    CONSTRAINT fk_project_synergy_to_project_description_c2_relevance FOREIGN KEY (project_relevance_id) REFERENCES project_description_c2_relevance (project_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_description_c3_partnership
(
    project_id INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_description_c3_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_description_c8_long_term_plans
(
    project_id              INT UNSIGNED PRIMARY KEY,
    CONSTRAINT fk_project_long_term_plans_to_project FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_partner_budget_staff_cost
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    type            ENUM ('REAL_COST','UNIT_COST')   DEFAULT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    row_sum         DECIMAL(17, 2) UNSIGNED NOT NULL,
    unit_cost_id    INT UNSIGNED                     DEFAULT NULL,
    CONSTRAINT fk_project_partner_budget_staff_cost_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_partner_budget_staff_cost_transl
(
    budget_id   INT UNSIGNED NOT NULL,
    language    VARCHAR(3) NOT NULL,
    unit_type   TEXT(100) DEFAULT NULL,
    description TEXT(255) DEFAULT NULL,
    comment     TEXT(255) DEFAULT NULL,
    PRIMARY KEY (budget_id, language),
    CONSTRAINT fk_project_partner_budget_staff_cost_transl_to_project FOREIGN KEY (budget_id) REFERENCES project_partner_budget_staff_cost (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_partner_budget_travel_transl
(
    budget_id   INT UNSIGNED NOT NULL,
    language    VARCHAR(3)   NOT NULL,
    description TEXT(255) DEFAULT NULL,
    unit_type TEXT(100) DEFAULT NULL,
    PRIMARY KEY (budget_id, language),
    CONSTRAINT fk_project_partner_budget_travel_transl_to_project FOREIGN KEY (budget_id) REFERENCES project_partner_budget_travel (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_partner_budget_external_transl
(
    budget_id        INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    award_procedures TEXT(250) DEFAULT NULL,
    PRIMARY KEY (budget_id, language),
    CONSTRAINT fk_project_partner_budget_external_transl_to_project FOREIGN KEY (budget_id) REFERENCES project_partner_budget_external (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_partner_budget_equipment_transl
(
    budget_id        INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    award_procedures TEXT(250) DEFAULT NULL,
    PRIMARY KEY (budget_id, language),
    CONSTRAINT fk_project_partner_budget_equipment_transl_to_project FOREIGN KEY (budget_id) REFERENCES project_partner_budget_equipment (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

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

CREATE TABLE project_partner_budget_infrastructure_transl
(
    budget_id        INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    award_procedures TEXT(250) DEFAULT NULL,
    PRIMARY KEY (budget_id, language),
    CONSTRAINT fk_project_partner_budget_infrastructure_transl_to_project FOREIGN KEY (budget_id) REFERENCES project_partner_budget_infrastructure (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_partner_co_financing
(
    partner_id        INT UNSIGNED                                               NOT NULL,
    type              ENUM ('PartnerContribution', 'MainFund', 'AdditionalFund') NOT NULL,
    percentage        DECIMAL(11, 2)                                             NOT NULL,
    programme_fund_id INT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (partner_id, type),
    CONSTRAINT fk_project_partner_co_financing_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_co_financing_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
);

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
       ('JA', false, false, false),
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
    indicator_result_id INT UNSIGNED   DEFAULT NULL,
    target_value        DECIMAL(11, 2) DEFAULT NULL,
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
