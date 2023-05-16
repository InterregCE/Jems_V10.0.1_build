CREATE TABLE controller_institution_partner_v2
(
    partner_id           INT UNSIGNED PRIMARY KEY,
    institution_id       INT UNSIGNED DEFAULT NULL,

    partner_number       INT                              NOT NULL,
    partner_abbreviation VARCHAR(15)                      NOT NULL,
    partner_role         ENUM ('PARTNER', 'LEAD_PARTNER') NOT NULL,
    partner_active       BOOLEAN                          NOT NULL,

    address_nuts3        VARCHAR(100)   DEFAULT NULL,
    address_nuts3_code   VARCHAR(5)   DEFAULT NULL,
    address_country      VARCHAR(100) DEFAULT NULL,
    address_country_code VARCHAR(2)   DEFAULT NULL,
    address_city         VARCHAR(50)  DEFAULT NULL,
    address_postal_code  VARCHAR(20)  DEFAULT NULL,

    project_identifier   VARCHAR(31)                      NOT NULL,
    project_acronym      VARCHAR(25)                      NOT NULL,

    CONSTRAINT fk_controller_institution_partner_to_partner
        FOREIGN KEY (partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_controller_institution_partner_to_institution
        FOREIGN KEY (institution_id) REFERENCES controller_institution (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

INSERT INTO controller_institution_partner_v2 (
    partner_id,
    institution_id,
    partner_number,
    partner_abbreviation,
    partner_role,
    partner_active,
    address_nuts3,
    address_nuts3_code,
    address_country,
    address_country_code,
    address_city,
    address_postal_code,
    project_identifier,
    project_acronym)
SELECT projectPartner.id,
       cip.institution_id,
       projectPartner.sort_number,
       projectPartner.abbreviation,
       projectPartner.role,
       projectPartner.active,
       partnerAddress.nuts_region3,
       partnerAddress.nuts_region3_code,
       partnerAddress.country,
       partnerAddress.country_code,
       partnerAddress.city,
       partnerAddress.postal_code,
       project.custom_identifier,
       project.acronym
FROM optimization_project_version AS opv
    INNER JOIN project FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS project
            ON project.id = opv.project_id
    INNER JOIN project_partner FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS projectPartner
            ON project.id = projectPartner.project_id
    LEFT JOIN project_partner_address FOR SYSTEM_TIME AS OF TIMESTAMP opv.last_approved_version AS partnerAddress
            ON projectPartner.id = partnerAddress.partner_id AND partnerAddress.type = 'Organization'
    LEFT JOIN controller_institution_partner AS cip
            ON cip.partner_id = projectPartner.id;
