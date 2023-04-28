CREATE TABLE report_project_wp_investment
(
    id                               INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_work_package_id           INT UNSIGNED   NOT NULL,
    number                           INT            NOT NULL,
    deactivated                      BOOLEAN        NOT NULL DEFAULT FALSE,
    expected_delivery_period         SMALLINT UNSIGNED       DEFAULT NULL,
    country                          VARCHAR(100) DEFAULT NULL,
    country_code                     VARCHAR(100) DEFAULT NULL,
    nuts_region2                     VARCHAR(100) DEFAULT NULL,
    nuts_region2_code                VARCHAR(100) DEFAULT NULL,
    nuts_region3                     VARCHAR(100) DEFAULT NULL,
    nuts_region3_code                VARCHAR(100) DEFAULT NULL,
    street                           VARCHAR(50)  DEFAULT NULL,
    house_number                     VARCHAR(20)  DEFAULT NULL,
    postal_code                      VARCHAR(20)  DEFAULT NULL,
    city                             VARCHAR(50)  DEFAULT NULL,
    homepage                         VARCHAR(250) DEFAULT NULL,
    CONSTRAINT fk_report_wp_investment_to_report_project_wp
        FOREIGN KEY (report_work_package_id) REFERENCES report_project_wp (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

CREATE TABLE report_project_wp_investment_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    title                                 VARCHAR(50),
    justification_explanation             TEXT(2000),
    justification_transactional_relevance TEXT(2000),
    justification_benefits                TEXT(2000),
    justification_pilot                   TEXT(2000),
    risk                                  TEXT(2000),
    documentation                         TEXT(2000),
    documentation_expected_impacts        TEXT(2000),
    ownership_site_location               VARCHAR(500),
    ownership_retain                      VARCHAR(500),
    ownership_maintenance                 TEXT(2000),
    progress                              TEXT(2000)   NOT NULL DEFAULT '',

    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_project_wp_investment_transl_to_report_wp_investment
        FOREIGN KEY (source_entity_id) REFERENCES report_project_wp_investment (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
