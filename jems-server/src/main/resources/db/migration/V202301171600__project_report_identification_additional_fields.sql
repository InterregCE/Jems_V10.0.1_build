CREATE TABLE report_project_identification_tg
(
    id                       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_report_id        INT UNSIGNED NOT NULL,
    type                     VARCHAR(127) NOT NULL,
    sort_number              INT          NOT NULL,
    CONSTRAINT fk_report_project_identification_tg_to_report_project_id
        FOREIGN KEY (project_report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_identification_tg_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    description      TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_ident_tg_transl_to_report_project_ident_tg
        FOREIGN KEY (source_entity_id) REFERENCES report_project_identification_tg (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_identification_transl
(
    source_entity_id        INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    highlights              TEXT(5000) DEFAULT NULL,
    partner_problems        TEXT(5000) DEFAULT NULL,
    deviations              TEXT(5000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_report_identification_transl_to_project_report_id
        FOREIGN KEY (source_entity_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
