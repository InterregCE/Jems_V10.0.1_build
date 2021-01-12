CREATE TABLE project_result_transl
(
    result_id                BINARY(16) NOT NULL,
    language                 VARCHAR(3) NOT NULL,
    description              TEXT(255) DEFAULT NULL,
    PRIMARY KEY (result_id, language),
    CONSTRAINT fk_project_result_transl_to_project_result FOREIGN KEY (result_id) REFERENCES project_result (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
