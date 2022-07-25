CREATE TABLE controller_institution_partner (

    institution_id INT UNSIGNED NOT NULL,
    partner_id INT UNSIGNED NOT NULL,
    partner_project_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (partner_id),
    CONSTRAINT fk_controller_institution_to_institution_id
        FOREIGN KEY (institution_id) REFERENCES controller_institution (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_partner_to_partner_id
        FOREIGN KEY (partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_project_to_partner_project_id
        FOREIGN KEY (partner_project_id) REFERENCES project (id)
            ON DELETE CASCADE
);
