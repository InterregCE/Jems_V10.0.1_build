DROP TABLE project_file;

CREATE TABLE project_file
(
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255)                              NOT NULL,
    project_id  INT UNSIGNED                              NOT NULL,
    user_id     INT UNSIGNED                              NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    size        BIGINT                                    NOT NULL,
    updated     DATETIME(3)  DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    CONSTRAINT fk_project_file_to_account
        FOREIGN KEY (user_id) REFERENCES account (id),
    CONSTRAINT fk_project_file_to_project
        FOREIGN KEY (project_id) REFERENCES project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_file_category
(
    file_id INT UNSIGNED,
    type   VARCHAR(255) NOT NULL,
    PRIMARY KEY (file_id, type),
    CONSTRAINT fk_project_file_category_to_project_file
        FOREIGN KEY (file_id) REFERENCES project_file (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
