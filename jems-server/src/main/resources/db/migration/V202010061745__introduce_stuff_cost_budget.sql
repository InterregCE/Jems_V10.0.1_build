CREATE TABLE project_partner_budget_staff_cost
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id      INT UNSIGNED            NOT NULL,
    number_of_units DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 1.00,
    price_per_unit  DECIMAL(17, 2) UNSIGNED NOT NULL,
    CONSTRAINT fk_project_partner_budget_staff_cost_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
