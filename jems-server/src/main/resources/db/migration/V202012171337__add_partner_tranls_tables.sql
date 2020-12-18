/*
 moves text fields of the following 3 tables into own translation tables
   - project_partner
   - project_partner_motivation
   - project_associated_organization
*/

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

ALTER TABLE project_partner
    DROP COLUMN department;

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

ALTER TABLE project_partner_motivation DROP COLUMN organization_relevance;
ALTER TABLE project_partner_motivation DROP COLUMN organization_role;
ALTER TABLE project_partner_motivation DROP COLUMN organization_experience;

CREATE TABLE project_associated_organization_transl
(
    organization_id         INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    role_description        TEXT DEFAULT NULL,
    PRIMARY KEY (organization_id, language),
    CONSTRAINT fk_project_associated_organization_transl_to_organization FOREIGN KEY (organization_id) REFERENCES project_associated_organization (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_associated_organization
    DROP COLUMN role_description;
