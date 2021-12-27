SET @@system_versioning_alter_history = 1;

ALTER TABLE project_partner
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE project_associated_organization
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
