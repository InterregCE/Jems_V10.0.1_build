/*
 moves the description of the following 5 tables into own translation tables
   - project_partner_budget_equipment
   - project_partner_budget_external
   - project_partner_budget_infrastructure
   - project_partner_budget_staff_cost
   - project_partner_budget_travel
*/

CREATE TABLE project_partner_budget_equipment_transl
(
    partner_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    description             TEXT(255) DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_budget_equipment_transl_to_project FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_equipment
    DROP COLUMN description;

CREATE TABLE project_partner_budget_external_transl
(
    partner_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    description             TEXT(255) DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_budget_external_transl_to_project FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_external
    DROP COLUMN description;

CREATE TABLE project_partner_budget_infrastructure_transl
(
    partner_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    description             TEXT(255) DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_budget_infrastructure_transl_to_project FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_infrastructure
    DROP COLUMN description;

CREATE TABLE project_partner_budget_staff_cost_transl
(
    partner_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    description             TEXT(255) DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_budget_staff_cost_transl_to_project FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_staff_cost
    DROP COLUMN description;

CREATE TABLE project_partner_budget_travel_transl
(
    partner_id              INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    description             TEXT(255) DEFAULT NULL,
    PRIMARY KEY (partner_id, language),
    CONSTRAINT fk_project_partner_budget_travel_transl_to_project FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_travel
    DROP COLUMN description;
