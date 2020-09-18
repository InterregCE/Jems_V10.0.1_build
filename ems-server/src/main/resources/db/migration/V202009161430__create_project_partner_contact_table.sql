CREATE TABLE partner_contact_person
(
    partner_id         INTEGER NOT NULL,
    type               ENUM ('LegalRepresentative', 'ContactPerson') NOT NULL,
    title              VARCHAR(25) DEFAULT NULL,
    first_name         VARCHAR(50) DEFAULT NULL,
    last_name          VARCHAR(50) DEFAULT NULL,
    email              VARCHAR(255) DEFAULT NULL,
    telephone          VARCHAR(25) DEFAULT NULL,
    CONSTRAINT pk_partner_contact_person PRIMARY KEY (partner_id, type),
    CONSTRAINT fk_project_partner_contact_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
