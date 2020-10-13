DROP TABLE project_partner_organization_details;

ALTER TABLE project_partner
    DROP FOREIGN KEY fk_project_partner_to_project_partner_organization;

ALTER TABLE project_partner
    DROP COLUMN organization_id;

DROP TABLE project_partner_organization;

RENAME TABLE partner_contact_person to project_partner_contact;

ALTER TABLE project_partner
    CHANGE COLUMN name abbreviation      VARCHAR(15),
    ADD COLUMN name_in_original_language VARCHAR(127) DEFAULT NULL AFTER sort_number,
    ADD COLUMN name_in_english           VARCHAR(127) DEFAULT NULL AFTER name_in_original_language,
    ADD COLUMN department                VARCHAR(255) DEFAULT NULL AFTER name_in_english;

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
