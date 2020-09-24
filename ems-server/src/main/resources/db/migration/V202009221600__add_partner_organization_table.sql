CREATE TABLE project_partner_organization
(
    id                        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name_in_original_language VARCHAR(100)    DEFAULT NULL,
    name_in_english           VARCHAR(100)    DEFAULT NULL,
    department                VARCHAR(250)    DEFAULT NULL
);

ALTER TABLE project_partner ADD COLUMN organization_id INT UNSIGNED AFTER sort_number;

ALTER TABLE project_partner ADD CONSTRAINT fk_project_partner_to_project_partner_organization FOREIGN KEY (organization_id) REFERENCES project_partner_organization (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
