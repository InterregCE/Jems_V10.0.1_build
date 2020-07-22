ALTER TABLE project_file
    ADD COLUMN type VARCHAR(127) NOT NULL AFTER author_id;
