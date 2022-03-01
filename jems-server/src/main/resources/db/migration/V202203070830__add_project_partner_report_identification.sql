CREATE TABLE report_project_partner_identification
(
    report_id     INT UNSIGNED NOT NULL PRIMARY KEY,
    start_date    DATE DEFAULT NULL,
    end_date      DATE DEFAULT NULL,
    period_number SMALLINT UNSIGNED DEFAULT NULL,
    CONSTRAINT fk_report_partner_identification_to_report_partner
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_identification_transl
(
    source_entity_id        INT UNSIGNED NOT NULL,
    language                VARCHAR(3) NOT NULL,
    summary                 TEXT(2000) DEFAULT NULL,
    problems_and_deviations TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_partner_report_id_transl_to_partner_report_id
        FOREIGN KEY (source_entity_id) REFERENCES report_project_partner_identification (report_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_identification_tg
(
    id                       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_identification_id INT UNSIGNED NOT NULL,
    type                     VARCHAR(127) NOT NULL,
    sort_number              INT          NOT NULL,
    CONSTRAINT fk_report_partner_id_tg_to_report_partner_id
        FOREIGN KEY (report_identification_id) REFERENCES report_project_partner_identification (report_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_identification_tg_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    specification    TEXT(2000) DEFAULT NULL,
    description      TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_partner_report_id_tg_transl_to_partner_report_id_tg
        FOREIGN KEY (source_entity_id) REFERENCES report_project_partner_identification_tg (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
