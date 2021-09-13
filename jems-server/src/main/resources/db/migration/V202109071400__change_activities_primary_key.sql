-- replace activity PK in all necessary tables
-- add activity relation to partner state aid
DROP TABLE project_work_package_activity_partner;
DROP TABLE project_work_package_activity_deliverable_transl;
DROP TABLE project_work_package_activity_deliverable;
DROP TABLE project_work_package_activity_transl;
DROP TABLE project_work_package_activity;

CREATE TABLE project_work_package_activity
(
    id              INT UNSIGNED     AUTO_INCREMENT PRIMARY KEY,
    work_package_id INT UNSIGNED     NOT NULL,
    activity_number TINYINT UNSIGNED NOT NULL,
    start_period    SMALLINT UNSIGNED DEFAULT NULL,
    end_period      SMALLINT UNSIGNED DEFAULT NULL,
    CONSTRAINT fk_project_work_package_activity_to_project_work_package
        FOREIGN KEY (work_package_id)
            REFERENCES project_work_package (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
ALTER TABLE project_work_package_activity
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    title            VARCHAR(200) DEFAULT NULL,
    description      TEXT(500)    DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_work_package_activity_transl_to_project_work_pkg_acti
        FOREIGN KEY (source_entity_id)
            REFERENCES project_work_package_activity (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
ALTER TABLE project_work_package_activity_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity_deliverable
(
    id                 INT UNSIGNED     AUTO_INCREMENT PRIMARY KEY,
    activity_id        INT UNSIGNED     NOT NULL,
    deliverable_number TINYINT UNSIGNED NOT NULL,
    start_period       SMALLINT UNSIGNED DEFAULT NULL,
    CONSTRAINT fk_project_work_package_activity_d_to_project_work_package_activ
        FOREIGN KEY (activity_id)
            REFERENCES project_work_package_activity (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
ALTER TABLE project_work_package_activity_deliverable
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity_deliverable_transl
(
    source_entity_id    INT UNSIGNED     NOT NULL,
    language           VARCHAR(3)       NOT NULL,
    description        TEXT(200) DEFAULT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_project_work_package_activity_del_transl_to_prjct_wrk_pckg_ad
        FOREIGN KEY (source_entity_id)
            REFERENCES project_work_package_activity_deliverable (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
ALTER TABLE project_work_package_activity_deliverable_transl
    ADD SYSTEM VERSIONING;

CREATE TABLE project_work_package_activity_partner
(
    activity_id        INT UNSIGNED NOT NULL,
    project_partner_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (activity_id, project_partner_id),
    CONSTRAINT fk_project_wp_activity_partner_to_project_wp
        FOREIGN KEY (activity_id) REFERENCES project_work_package_activity (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_partner_to_project_partner
        FOREIGN KEY (project_partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
);
ALTER TABLE project_work_package_activity_partner
    ADD SYSTEM VERSIONING;

CREATE TABLE project_partner_state_aid_activity
(
    activity_id        INT UNSIGNED NOT NULL,
    project_partner_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (activity_id, project_partner_id),
    CONSTRAINT fk_project_partner_state_aid_to_activity
        FOREIGN KEY (activity_id) REFERENCES project_work_package_activity (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_partner_state_aid_to_partner
        FOREIGN KEY (project_partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
);
ALTER TABLE project_partner_state_aid_activity
    ADD SYSTEM VERSIONING;

