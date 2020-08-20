CREATE TABLE project_partner (
    id          INTEGER AUTO_INCREMENT PRIMARY KEY,
    project_id  INTEGER NOT NULL,
    name        VARCHAR(15) NOT NULL,
    role        VARCHAR(127) NOT NULL,
    CONSTRAINT fk_project_partner_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    UNIQUE KEY project_partner_project_name (project_id, name)
);
