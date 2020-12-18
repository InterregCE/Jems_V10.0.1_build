CREATE TABLE project_work_package_activity
(
    work_package_id INT UNSIGNED     NOT NULL,
    activity_number TINYINT UNSIGNED NOT NULL,
    start_period    SMALLINT UNSIGNED DEFAULT NULL,
    end_period      SMALLINT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (work_package_id, activity_number),
    CONSTRAINT fk_project_work_package_activity_to_project_work_package
        FOREIGN KEY (work_package_id)
            REFERENCES project_work_package (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_work_package_activity_transl
(
    work_package_id INT UNSIGNED     NOT NULL,
    activity_number TINYINT UNSIGNED NOT NULL,
    language VARCHAR(3) NOT NULL,
    title           VARCHAR(200)      DEFAULT NULL,
    description     TEXT(500)         DEFAULT NULL,
    PRIMARY KEY (work_package_id, activity_number, language),
    CONSTRAINT fk_project_work_package_activity_transl_to_project_work_pkg_acti
        FOREIGN KEY (work_package_id, activity_number)
            REFERENCES project_work_package_activity (work_package_id, activity_number)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_work_package_activity_deliverable
(
    work_package_id    INT UNSIGNED     NOT NULL,
    activity_number    TINYINT UNSIGNED NOT NULL,
    deliverable_number TINYINT UNSIGNED NOT NULL,
    start_period       SMALLINT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (work_package_id, activity_number, deliverable_number),
    CONSTRAINT fk_project_work_package_activity_d_to_project_work_package_activ
        FOREIGN KEY (work_package_id, activity_number)
            REFERENCES project_work_package_activity (work_package_id, activity_number)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_work_package_activity_deliverable_transl
(
    work_package_id    INT UNSIGNED     NOT NULL,
    activity_number    TINYINT UNSIGNED NOT NULL,
    deliverable_number TINYINT UNSIGNED NOT NULL,
    language VARCHAR(3) NOT NULL,
    description        TEXT(200)         DEFAULT NULL,
    PRIMARY KEY (work_package_id, activity_number, deliverable_number, language),
    CONSTRAINT fk_project_work_package_activity_del_transl_to_prjct_wrk_pckg_ad
        FOREIGN KEY (work_package_id, activity_number, deliverable_number)
            REFERENCES project_work_package_activity_deliverable (work_package_id, activity_number, deliverable_number)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
