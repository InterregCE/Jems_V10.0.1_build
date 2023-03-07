CREATE TABLE report_project_result
(
    id                           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    report_id                    INT UNSIGNED     NOT NULL,
    result_number                TINYINT UNSIGNED NOT NULL,
    period_number                SMALLINT UNSIGNED,
    indicator_result_id          INT UNSIGNED              DEFAULT NULL,
    baseline                     DECIMAL(17, 2)   NOT NULL DEFAULT 0,
    target_value                 DECIMAL(17, 2)            DEFAULT NULL,
    current_report               DECIMAL(17, 2)   NOT NULL DEFAULT 0,
    previously_reported          DECIMAL(17, 2)   NOT NULL DEFAULT 0,
    attachment_id                INT UNSIGNED              DEFAULT NULL,
    CONSTRAINT fk_report_project_result_to_project_report
        FOREIGN KEY (report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_project_result_indicator_to_programme_indicator_result
        FOREIGN KEY (indicator_result_id) REFERENCES programme_indicator_result (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_project_result_attachment_to_project_report_file
        FOREIGN KEY (attachment_id) REFERENCES file_metadata (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_result_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,

    description      TEXT(2000) DEFAULT NULL,

    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_result_transl_to_report_project_result
        FOREIGN KEY (source_entity_id) REFERENCES report_project_result (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_horizontal_principle
(
    report_id                               INT UNSIGNED NOT NULL,

    sustainable_development_criteria_effect ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,
    equal_opportunities_effect              ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,
    sexual_equality_effect                  ENUM ('PositiveEffects', 'Neutral', 'NegativeEffects') DEFAULT NULL,

    PRIMARY KEY (report_id),
    CONSTRAINT fk_report_project_horizontal_principle_to_project_report_id
        FOREIGN KEY (report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_horizontal_principle_transl
(
    source_entity_id                    INT UNSIGNED NOT NULL,
    language                            VARCHAR(3)   NOT NULL,

    sustainable_development_description TEXT DEFAULT NULL,
    equal_opportunities_description     TEXT DEFAULT NULL,
    sexual_equality_description         TEXT DEFAULT NULL,

    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_proj_report_principle_transl_to_proj_report_result_principle
        FOREIGN KEY (source_entity_id)
            REFERENCES report_project_horizontal_principle (report_id)
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
        'ProjectResult'
        ) NOT NULL;
