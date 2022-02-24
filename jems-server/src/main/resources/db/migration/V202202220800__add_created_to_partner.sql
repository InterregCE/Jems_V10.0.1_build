SET @@system_versioning_alter_history = 1;
ALTER TABLE project_partner
    ADD COLUMN created_at DATETIME(3) NOT NULL DEFAULT current_timestamp (3)
