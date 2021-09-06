DROP TABLE project_work_package_activity;
DROP TABLE project_work_package_activity_transl;
DROP TABLE project_work_package_activity_deliverable;
DROP TABLE project_work_package_activity_deliverable_transl;

CREATE TABLE project_work_package_activity
(
    activity_id     INT UNSIGNED     NOT NULL,
    work_package_id INT UNSIGNED     NOT NULL,
    activity_number TINYINT UNSIGNED NOT NULL,
    start_period    SMALLINT UNSIGNED DEFAULT NULL,
    end_period      SMALLINT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (activity_id),
    CONSTRAINT fk_project_work_package_activity_to_project_work_package
        FOREIGN KEY (work_package_id)
            REFERENCES project_work_package (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_work_package_activity_transl
(
    activity_id INT UNSIGNED NOT NULL,
    language    VARCHAR(3)   NOT NULL,
    title       VARCHAR(200) DEFAULT NULL,
    description TEXT(500)    DEFAULT NULL,
    PRIMARY KEY (activity_id, language),
    CONSTRAINT fk_project_work_package_activity_transl_to_project_work_pkg_acti
        FOREIGN KEY (activity_id)
            REFERENCES project_work_package_activity (activity_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_work_package_activity_deliverable
(
    deliverable_id     INT UNSIGNED     NOT NULL,
    activity_id        INT UNSIGNED     NOT NULL,
    deliverable_number TINYINT UNSIGNED NOT NULL,
    start_period       SMALLINT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (deliverable_id),
    CONSTRAINT fk_project_work_package_activity_d_to_project_work_package_activ
        FOREIGN KEY (activity_id)
            REFERENCES project_work_package_activity (activity_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE project_work_package_activity_deliverable_transl
(
    deliverable_id    INT UNSIGNED     NOT NULL,
    language           VARCHAR(3)       NOT NULL,
    description        TEXT(200) DEFAULT NULL,
    PRIMARY KEY (deliverable_id, language),
    CONSTRAINT fk_project_work_package_activity_del_transl_to_prjct_wrk_pckg_ad
        FOREIGN KEY (deliverable_id)
            REFERENCES project_work_package_activity_deliverable (deliverable_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);




