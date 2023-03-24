CREATE TABLE report_project_certificate_investment
(
    id                                                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id                                         INT UNSIGNED NOT NULL,
    investment_id                                     INT UNSIGNED NOT NULL,
    investment_number                                 INT UNSIGNED NOT NULL,
    work_package_number                               INT UNSIGNED NOT NULL,
    deactivated                                       BOOLEAN NOT NULL DEFAULT FALSE,

    total                                             DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    current                                           DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    previously_reported                               DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_report_project_certificate_investment_to_project_report
        FOREIGN KEY (report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_certificate_investment_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    title            VARCHAR(50)  NOT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_investment_transl_to_report_project_investment
        FOREIGN KEY (source_entity_id) REFERENCES report_project_certificate_investment (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
