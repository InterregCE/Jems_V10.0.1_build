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
       ('ISO2');

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
       ('PO1', 'Digitalization'),
       ('PO1', 'Growth'),
       ('PO1', 'IndustrialTransition'),

       ('PO2', 'EnergyEfficiency'),
       ('PO2', 'RenewableEnergy'),
       ('PO2', 'SmartEnergy'),
       ('PO2', 'ClimateChange'),
       ('PO2', 'WaterManagement'),
       ('PO2', 'CircularEconomy'),
       ('PO2', 'GreenUrban'),

       ('PO3', 'DigitalConnectivity'),
       ('PO3', 'InterModalTenT'),
       ('PO3', 'CrossBorderMobility'),
       ('PO3', 'MultiModalUrban'),

       ('PO4', 'SocialInnovation'),
       ('PO4', 'Infrastructure'),
       ('PO4', 'DisadvantagedGroups'),
       ('PO4', 'Healthcare'),
       ('PO4', 'EmploymentAcrossBorders'),
       ('PO4', 'LearningAcrossBorders'),
       ('PO4', 'HealthcareAcrossBorders'),
       ('PO4', 'LongTermHealthcareAcrossBorders'),
       ('PO4', 'EqualOpportunitiesAcrossBorders'),

       ('PO5', 'EnvDevelopment'),
       ('PO5', 'LocalEnvDevelopment'),

       ('ISO1', 'ISO1PublicAuthorities'),
       ('ISO1', 'ISO1AdministrativeCooperation'),
       ('ISO1', 'ISO1IncreaseTrust'),
       ('ISO1', 'ISO1MacroRegion'),
       ('ISO1', 'ISO1Democracy'),
       ('ISO1', 'ISO1Other'),

       ('ISO2', 'ISO2PublicAuthorities'),
       ('ISO2', 'ISO2AdministrativeCooperation'),
       ('ISO2', 'ISO2IncreaseTrust'),
       ('ISO2', 'ISO2MacroRegion'),
       ('ISO2', 'ISO2Democracy'),
       ('ISO2', 'ISO2Other');

CREATE TABLE programme_priority
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(50)  NOT NULL UNIQUE,
    title        VARCHAR(300) NOT NULL UNIQUE,
    objective_id VARCHAR(7)   NOT NULL,
    CONSTRAINT fk_programme_priority_programme_objective
        FOREIGN KEY (objective_id) REFERENCES programme_objective (code)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
);

CREATE TABLE programme_priority_policy
(
    programme_objective_policy_code VARCHAR(127) PRIMARY KEY,
    programme_priority_id           INTEGER NOT NULL,
    code                            VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT fk_programme_priority_policy_programme_objective_policy
        FOREIGN KEY (programme_objective_policy_code) REFERENCES programme_objective_policy (code)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT fk_programme_priority_policy_programme_priority
        FOREIGN KEY (programme_priority_id) REFERENCES programme_priority (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

DELIMITER $$

CREATE TRIGGER priority_policy_should_have_correct_programme_objective_insert
    BEFORE INSERT
    ON programme_priority_policy
    FOR EACH ROW
BEGIN
    DECLARE objective_for_programme VARCHAR(7);
    DECLARE objective_for_policy VARCHAR(7);

    SELECT objective_id FROM programme_priority WHERE id = NEW.programme_priority_id INTO objective_for_programme;
    SELECT objective_id
    FROM programme_objective_policy
    WHERE code = NEW.programme_objective_policy_code
    INTO objective_for_policy;

    IF objective_for_programme != objective_for_policy THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =
                'programme.priority.priorityPolicies.should.not.be.of.different.objectives';
    END IF;
END$$

CREATE TRIGGER priority_policy_should_have_correct_programme_objective_update
    BEFORE UPDATE
    ON programme_priority_policy
    FOR EACH ROW
BEGIN
    DECLARE objective_for_programme VARCHAR(7);
    DECLARE objective_for_policy VARCHAR(7);

    SELECT objective_id FROM programme_priority WHERE code = NEW.programme_priority_id INTO objective_for_programme;
    SELECT objective_id
    FROM programme_objective_policy
    WHERE code = NEW.programme_objective_policy_code
    INTO objective_for_policy;

    IF objective_for_programme != objective_for_policy THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =
                'programme.priority.priorityPolicies.should.not.be.of.different.objectives';
    END IF;
END$$

DELIMITER ;
