CREATE TABLE account_partner_collaborator
(
    account_id INT UNSIGNED NOT NULL,
    project_id INT UNSIGNED NOT NULL,
    partner_id INT UNSIGNED NOT NULL,
    level      ENUM('VIEW', 'EDIT') NOT NULL DEFAULT 'VIEW',
    PRIMARY KEY (account_id, partner_id),
    CONSTRAINT fk_apc_project_id_to_project
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_apc_account_id_to_account
        FOREIGN KEY (account_id)
            REFERENCES account (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_apc_partner_id_to_partner
        FOREIGN KEY (partner_id)
            REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
