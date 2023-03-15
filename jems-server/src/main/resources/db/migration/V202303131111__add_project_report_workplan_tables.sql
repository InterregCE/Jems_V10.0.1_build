CREATE TABLE report_project_wp
(
    id                   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id            INT UNSIGNED NOT NULL,
    number               INT          NOT NULL,
    deactivated          BOOLEAN      NOT NULL           DEFAULT FALSE,
    work_package_id      INT UNSIGNED                    DEFAULT NULL,
    specific_status      ENUM ('Fully', 'Partly', 'Not') DEFAULT NULL,
    communication_status ENUM ('Fully', 'Partly', 'Not') DEFAULT NULL,
    completed            BOOLEAN      NOT NULL           DEFAULT FALSE,
    CONSTRAINT fk_report_wp_to_report_project
        FOREIGN KEY (report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_wp_transl
(
    source_entity_id          INT UNSIGNED NOT NULL,
    language                  VARCHAR(3)   NOT NULL,

    specific_objective        TEXT(2000)   NOT NULL DEFAULT '',
    specific_explanation      TEXT(2000)   NOT NULL DEFAULT '',
    communication_objective   TEXT(2000)   NOT NULL DEFAULT '',
    communication_explanation TEXT(2000)   NOT NULL DEFAULT '',
    description               TEXT(2000)   NOT NULL DEFAULT '',

    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_wp_transl_to_report_wp
        FOREIGN KEY (source_entity_id) REFERENCES report_project_wp (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_wp_activity
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_work_package_id INT UNSIGNED NOT NULL,
    number                 INT          NOT NULL,
    deactivated            BOOLEAN      NOT NULL           DEFAULT FALSE,
    activity_id            INT UNSIGNED                    DEFAULT NULL,
    start_period_number    SMALLINT UNSIGNED               DEFAULT NULL,
    end_period_number      SMALLINT UNSIGNED               DEFAULT NULL,
    status                 ENUM ('Fully', 'Partly', 'Not') DEFAULT NULL,
    file_id                INT UNSIGNED                    DEFAULT NULL,
    CONSTRAINT fk_report_wp_activity_to_report_project_wp
        FOREIGN KEY (report_work_package_id) REFERENCES report_project_wp (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_proj_wp_activity_to_report_file
        FOREIGN KEY (file_id) REFERENCES file_metadata (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_wp_activity_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,

    title            VARCHAR(200) NOT NULL DEFAULT '',
    progress         TEXT(2000)   NOT NULL DEFAULT '',

    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_wp_activity_transl_to_report_wp_activity
        FOREIGN KEY (source_entity_id) REFERENCES report_project_wp_activity (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_wp_activity_deliverable
(
    id                  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_activity_id  INT UNSIGNED   NOT NULL,
    number              INT            NOT NULL,
    deactivated         BOOLEAN        NOT NULL DEFAULT FALSE,
    deliverable_id      INT UNSIGNED            DEFAULT NULL,
    period_number       SMALLINT UNSIGNED       DEFAULT NULL,
    previously_reported DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    current_report      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    file_id             INT UNSIGNED            DEFAULT NULL,
    CONSTRAINT fk_report_wp_activity_del_to_report_project_wp_activity
        FOREIGN KEY (report_activity_id) REFERENCES report_project_wp_activity (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_proj_wp_activity_deliverable_to_report_file
        FOREIGN KEY (file_id) REFERENCES file_metadata (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_wp_activity_deliverable_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,

    title            VARCHAR(200) NOT NULL DEFAULT '',
    progress         TEXT(2000)   NOT NULL DEFAULT '',

    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_wp_deliverable_transl_to_report_wp_deliverable
        FOREIGN KEY (source_entity_id) REFERENCES report_project_wp_activity_deliverable (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_wp_output
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_work_package_id INT UNSIGNED   NOT NULL,
    number                 INT            NOT NULL,
    deactivated            BOOLEAN        NOT NULL DEFAULT FALSE,
    indicator_output_id    INT UNSIGNED            DEFAULT NULL,
    period_number          SMALLINT UNSIGNED       DEFAULT NULL,
    target_value           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    previously_reported    DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    current_report         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    file_id                INT UNSIGNED            DEFAULT NULL,
    CONSTRAINT fk_report_wp_output_to_report_project_wp
        FOREIGN KEY (report_work_package_id) REFERENCES report_project_wp (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_proj_wp_output_to_report_file
        FOREIGN KEY (file_id) REFERENCES file_metadata (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_wp_output_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    title            VARCHAR(200) NOT NULL DEFAULT '',
    progress         TEXT(2000)   NOT NULL DEFAULT '',
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_wp_ouput_transl_to_report_wp_output
        FOREIGN KEY (source_entity_id) REFERENCES report_project_wp_output (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE file_metadata
    CHANGE COLUMN type type ENUM (
        'PartnerReport',
        'WorkPackage',
        'Activity',
        'Deliverable',
        'Output',
        'Expenditure',
        'ProcurementAttachment',
        'Contribution',
        'ControlDocument',
        'ControlCertificate',
        'ControlReport',
        'Contract',
        'ContractDoc',
        'ContractPartnerDoc',
        'ContractInternal',
        'PaymentAttachment',
        'PaymentAdvanceAttachment',
        'ProjectReport',
        'ProjectResult',
        'ActivityProjectReport',
        'DeliverableProjectReport',
        'OutputProjectReport'
) NOT NULL;
