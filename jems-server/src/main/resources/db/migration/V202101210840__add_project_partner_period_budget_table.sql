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

