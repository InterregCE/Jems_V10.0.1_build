CREATE TABLE report_project_partner_wp
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id       INT UNSIGNED NOT NULL,
    number          INT NOT NULL,
    work_package_id INT UNSIGNED DEFAULT NULL,
    CONSTRAINT fk_report_wp_to_report_project_partner
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_wp_to_work_package
        FOREIGN KEY (work_package_id)
            REFERENCES project_work_package (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_wp_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    description      TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_wp_transl_to_report_wp
        FOREIGN KEY (source_entity_id) REFERENCES report_project_partner_wp (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_wp_activity
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_work_package_id INT UNSIGNED NOT NULL,
    number                 INT NOT NULL,
    activity_id            INT UNSIGNED DEFAULT NULL,
    CONSTRAINT fk_report_wp_activity_to_report_project_partner_wp
        FOREIGN KEY (report_work_package_id) REFERENCES report_project_partner_wp (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_partner_wp_activity_to_wp_activity
        FOREIGN KEY (activity_id)
            REFERENCES project_work_package_activity (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_wp_activity_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    title            VARCHAR(200) DEFAULT NULL,
    description      TEXT(2000) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_wp_activity_transl_to_report_wp_activity
        FOREIGN KEY (source_entity_id) REFERENCES report_project_partner_wp_activity (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_wp_activity_deliverable
(
    id                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_activity_id INT UNSIGNED NOT NULL,
    number             INT NOT NULL,
    deliverable_id     INT UNSIGNED DEFAULT NULL,
    contribution       BOOLEAN DEFAULT NULL,
    evidence           BOOLEAN DEFAULT NULL,
    CONSTRAINT fk_report_wp_deliverable_to_report_wp_activity
        FOREIGN KEY (report_activity_id) REFERENCES report_project_partner_wp_activity (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_wp_deliverable_to_wp_activity_deliverable
        FOREIGN KEY (deliverable_id)
            REFERENCES project_work_package_activity_deliverable (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_wp_activity_deliverable_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    title            VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_wp_deliverable_transl_to_report_wp_deliverable
        FOREIGN KEY (source_entity_id) REFERENCES report_project_partner_wp_activity_deliverable (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_wp_output
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_work_package_id INT UNSIGNED NOT NULL,
    number                 INT NOT NULL,
    contribution           BOOLEAN DEFAULT NULL,
    evidence               BOOLEAN DEFAULT NULL,
    CONSTRAINT fk_report_wp_output_to_report_project_partner_wp
        FOREIGN KEY (report_work_package_id) REFERENCES report_project_partner_wp (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_partner_wp_output_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3) NOT NULL,
    title            VARCHAR(200) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_wp_output_transl_to_report_wp_output
        FOREIGN KEY (source_entity_id) REFERENCES report_project_partner_wp_output (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
