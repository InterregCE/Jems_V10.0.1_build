CREATE TABLE translation_file
(
    file_type     ENUM ('Application', 'System'),
    language      VARCHAR(3)  NOT NULL,
    last_modified DATETIME(3) NOT NULL DEFAULT current_timestamp(3),
    PRIMARY KEY (file_type, language)
);
