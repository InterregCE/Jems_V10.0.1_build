ALTER TABLE project_associated_organization
    ADD COLUMN role_description TEXT(2000) DEFAULT NULL AFTER sort_number;
