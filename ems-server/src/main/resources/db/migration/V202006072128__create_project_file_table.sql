CREATE TABLE project_file (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    bucket VARCHAR(100) NOT NULL,
    identifier VARCHAR(255) NOT NULL,
    project_id INTEGER NOT NULL,
    description VARCHAR(255),
    size BIGINT NOT NULL,
    updated DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_project_file_project
        FOREIGN KEY (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    UNIQUE KEY project_file_bucket_identifier (bucket, identifier)
);
