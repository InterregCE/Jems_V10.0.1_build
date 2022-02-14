CREATE TABLE project_partner_budget_spfcost
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    row_sum         DECIMAL(17, 2) UNSIGNED NOT NULL,
    unit_cost_id    INT UNSIGNED                     DEFAULT NULL,
    CONSTRAINT fk_project_partner_budget_spfcost_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_spfcost
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_spfcost_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    description      TEXT(255) DEFAULT NULL,
    unit_type        TEXT(100) DEFAULT NULL,
    comments         TEXT(255) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_partner_budget_spfcost_transl_to_project FOREIGN KEY (source_entity_id) REFERENCES project_partner_budget_spfcost (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_spfcost_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_budget_spfcost_period
(
    budget_id     INT UNSIGNED            NOT NULL,
    project_id    INT UNSIGNED            NOT NULL,
    period_number SMALLINT UNSIGNED       NOT NULL,
    amount        DECIMAL(17, 2) UNSIGNED NOT NULL,
    PRIMARY KEY (budget_id, project_id, period_number),
    CONSTRAINT fk_project_period_spfcost_cost FOREIGN KEY (project_id, period_number) REFERENCES project_period (project_id, number) ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_budget_spfcost FOREIGN KEY (budget_id) REFERENCES project_partner_budget_spfcost (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_spfcost_period
    ADD SYSTEM VERSIONING;
