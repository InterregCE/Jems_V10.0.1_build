CREATE TABLE project_partner_budget_office_administration
(
    partner_id INT UNSIGNED PRIMARY KEY,
    flat_rate  INT NOT NULL DEFAULT 15,
    CONSTRAINT fk_project_partner_budget_office_administrati_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
