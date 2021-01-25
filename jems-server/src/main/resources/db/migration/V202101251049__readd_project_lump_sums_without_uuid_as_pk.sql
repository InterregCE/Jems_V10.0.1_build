DROP TABLE project_partner_lump_sum;
DROP TABLE project_lump_sum;

CREATE TABLE project_lump_sum
(
    project_id            INT UNSIGNED     NOT NULL,
    order_nr              TINYINT UNSIGNED NOT NULL,
    programme_lump_sum_id INT UNSIGNED     NOT NULL,
    end_period            TINYINT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (project_id, order_nr),
    CONSTRAINT fk_project_lump_sum_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_lump_sum_to_programme_lump_sum
        FOREIGN KEY (programme_lump_sum_id) REFERENCES programme_lump_sum (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);

CREATE TABLE project_partner_lump_sum
(
    project_id INT UNSIGNED     NOT NULL,
    order_nr   TINYINT UNSIGNED NOT NULL,
    project_partner_id  INT UNSIGNED     NOT NULL,
    amount              DECIMAL(11, 2)   NOT NULL,
    PRIMARY KEY (project_id, order_nr, project_partner_id),
    CONSTRAINT fk_project_partner_lump_sum_to_project_lump_sum
        FOREIGN KEY (project_id, order_nr) REFERENCES project_lump_sum (project_id, order_nr)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_lump_sum_to_project_partner
        FOREIGN KEY (project_partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
