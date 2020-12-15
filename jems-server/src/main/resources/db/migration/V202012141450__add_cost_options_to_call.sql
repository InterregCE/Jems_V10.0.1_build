CREATE TABLE project_call_lump_sum
(
    project_call_id       INT UNSIGNED NOT NULL,
    programme_lump_sum_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (project_call_id, programme_lump_sum_id),
    CONSTRAINT fk_project_call_lump_sum_to_project_call
        FOREIGN KEY (project_call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_lump_sum_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id)
            REFERENCES programme_lump_sum (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

CREATE TABLE project_call_unit_cost
(
    project_call_id        INT UNSIGNED NOT NULL,
    programme_unit_cost_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (project_call_id, programme_unit_cost_id),
    CONSTRAINT fk_project_call_unit_cost_to_project_call
        FOREIGN KEY (project_call_id)
            REFERENCES project_call (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_call_unit_cost_to_programme_unit_cost
        FOREIGN KEY (programme_unit_cost_id)
            REFERENCES programme_unit_cost (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);
