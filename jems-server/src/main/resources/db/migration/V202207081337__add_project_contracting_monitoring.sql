CREATE TABLE project_contracting_monitoring
(
    project_id            INT UNSIGNED NOT NULL,
    start_date            DATETIME(3)  DEFAULT NULL,
    end_date              DATETIME(3)  DEFAULT NULL,
    typology_prov_94               ENUM ('Yes', 'No', 'Partly') DEFAULT NULL,
    typology_prov_94_comment       VARCHAR(1000)                DEFAULT NULL,
    typology_prov_95               ENUM ('Yes', 'No', 'Partly') DEFAULT NULL,
    typology_prov_95_comment       VARCHAR(1000)                DEFAULT NULL,
    typology_strategic             ENUM ('Yes', 'No')           DEFAULT NULL,
    typology_strategic_comment     VARCHAR(1000)                DEFAULT NULL,
    typology_partnership           ENUM ('Yes', 'No')           DEFAULT NULL,
    typology_partnership_comment   VARCHAR(1000)                DEFAULT NULL,
    PRIMARY KEY (project_id),
    CONSTRAINT fk_project_contracting_monitoring_project foreign key (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE project_contracting_monitoring_add_date
(
    project_id              INT UNSIGNED     NOT NULL,
    number                  TINYINT UNSIGNED NOT NULL,
    entry_into_force_date   DATETIME(3)  DEFAULT NULL,
    comment                 VARCHAR(200) DEFAULT NULL,
    PRIMARY KEY (project_id, number),
    CONSTRAINT fk_project_contracting_mon_add_date_project foreign key (project_id) REFERENCES project (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);
