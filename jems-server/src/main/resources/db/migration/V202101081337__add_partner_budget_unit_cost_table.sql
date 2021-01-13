/*
 add table for general unit costs for each project partner
*/

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
