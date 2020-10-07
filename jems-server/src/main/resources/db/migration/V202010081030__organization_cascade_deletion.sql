ALTER TABLE project_partner DROP FOREIGN KEY fk_project_partner_to_project_partner_organization;

ALTER TABLE project_partner ADD CONSTRAINT fk_project_partner_to_project_partner_organization FOREIGN KEY (organization_id) REFERENCES project_partner_organization (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
