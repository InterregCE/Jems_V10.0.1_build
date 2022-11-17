CREATE TABLE project_contracting_partner_beneficial_owner
(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id     INT UNSIGNED NOT NULL,
    first_name     TEXT(255) NOT NULL DEFAULT '',
    last_name       TEXT(255) NOT NULL DEFAULT '',
    vat_number      TEXT(30) NOT NULL DEFAULT '',
    birth          DATE,
    CONSTRAINT fk_project_contracting_partner_beneficial_owner_to_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
