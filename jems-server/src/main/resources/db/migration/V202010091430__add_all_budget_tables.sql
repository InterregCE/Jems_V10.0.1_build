CREATE TABLE project_partner_budget_external
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    description     VARCHAR(255)                     DEFAULT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    CONSTRAINT fk_project_partner_budget_external_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_partner_budget_equipment
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    description     VARCHAR(255)                     DEFAULT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    CONSTRAINT fk_project_partner_budget_equipment_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_partner_budget_infrastructure
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    description     VARCHAR(255)                     DEFAULT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    CONSTRAINT fk_project_partner_budget_infrastructure_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
