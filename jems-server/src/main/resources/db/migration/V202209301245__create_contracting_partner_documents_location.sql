CREATE TABLE project_contracting_partner_documents_location
(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id              INT UNSIGNED NOT NULL UNIQUE,
    first_name              TEXT(255) DEFAULT '',
    last_name               TEXT(255) DEFAULT '',
    title                   TEXT(255) DEFAULT '',
    email_address           TEXT(255) DEFAULT '',
    telephone_no            TEXT(25) DEFAULT '',
    institution_name        TEXT(100) DEFAULT '',
    street                  TEXT(50) DEFAULT '',
    location_number         TEXT(20) DEFAULT '',
    postal_code             TEXT(20) DEFAULT '',
    city                    TEXT(50) DEFAULT '',
    homepage                TEXT(100) DEFAULT '',
    country_code            TEXT(2) DEFAULT '',
    country                 TEXT(100) DEFAULT '',
    nuts_two_region_code    TEXT(4) DEFAULT '',
    nuts_two_region         TEXT(100) DEFAULT '',
    nuts_three_region_code  TEXT(5) DEFAULT '',
    nuts_three_region       TEXT(100) DEFAULT '',
    CONSTRAINT fk_project_contracting_partner_documents_location_to_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
