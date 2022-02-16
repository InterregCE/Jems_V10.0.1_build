CREATE TABLE report_project_partner
(
    id                        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    partner_id                INT UNSIGNED NOT NULL,
    number                    INT          NOT NULL,
    status                    ENUM ('Draft', 'Submitted') DEFAULT 'Draft',
    application_form_version  VARCHAR(127) NOT NULL,
    first_submission          DATETIME(3) DEFAULT NULL,

    -- identification tab:
    project_identifier        VARCHAR(31) NOT NULL,
    project_acronym           VARCHAR(25) NOT NULL,
    partner_number            INT         NOT NULL,
    partner_abbreviation      VARCHAR(15) NOT NULL,
    partner_role              ENUM ('PARTNER', 'LEAD_PARTNER') NOT NULL,
    name_in_original_language VARCHAR(127) DEFAULT NULL,
    name_in_english           VARCHAR(127) DEFAULT NULL,
    legal_status_id           INT UNSIGNED DEFAULT NULL,
    partner_type              VARCHAR(127) DEFAULT NULL,
    vat_recovery              ENUM ('Yes', 'No', 'Partly') DEFAULT NULL,

    created_at                DATETIME(3) NOT NULL DEFAULT current_timestamp (3),
    CONSTRAINT fk_report_project_partner_to_project_partner
        FOREIGN KEY (partner_id) REFERENCES project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_project_partner_to_programme_legal_status
        FOREIGN KEY (legal_status_id)
            REFERENCES programme_legal_status (id)
            ON DELETE SET NULL
            ON UPDATE RESTRICT,
    UNIQUE KEY project_partner_report_number (partner_id, number)
);

CREATE TABLE report_project_partner_co_financing
(
    report_id         INT UNSIGNED NOT NULL,
    fund_sort_number  TINYINT UNSIGNED NOT NULL,
    programme_fund_id INT UNSIGNED DEFAULT NULL,
    percentage        DECIMAL(11, 2) NOT NULL,
    PRIMARY KEY (report_id, fund_sort_number),
    CONSTRAINT fk_report_partner_co_financing_to_report_project_partner
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_partner_to_programme_fund
        FOREIGN KEY (programme_fund_id)
            REFERENCES programme_fund (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
