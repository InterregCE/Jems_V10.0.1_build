ALTER TABLE programme_unit_cost
    ADD COLUMN project_id INT UNSIGNED DEFAULT NULL,
    ADD CONSTRAINT fk_programme_unit_cost_to_project FOREIGN KEY(project_id) REFERENCES project(id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    ADD SYSTEM VERSIONING;

ALTER TABLE programme_unit_cost_budget_category
    ADD SYSTEM VERSIONING;

ALTER TABLE programme_unit_cost_transl
    ADD COLUMN justification TEXT(5000) DEFAULT NULL,
    ADD SYSTEM VERSIONING;
