CREATE TABLE programme_checklist
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    type                   VARCHAR(50) NOT NULL,
    name                   VARCHAR(127),
    last_modification_date DATETIME(3)
);

CREATE TABLE programme_checklist_component
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    type                   VARCHAR(50)  NOT NULL,
    position_on_table      SMALLINT UNSIGNED NOT NULL,
    programme_checklist_id INT UNSIGNED NOT NULL,
    metadata               JSON,

    CHECK (JSON_VALID(metadata)),
    CONSTRAINT fk_programme_checklist_programme_check_list_component
        FOREIGN KEY (programme_checklist_id) REFERENCES programme_checklist (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
