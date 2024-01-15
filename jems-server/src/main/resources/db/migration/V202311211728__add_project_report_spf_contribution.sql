CREATE TABLE report_project_spf_contribution_claim
(
    id                                              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id                                       INT UNSIGNED NOT NULL,
    programme_fund_id                               INT UNSIGNED DEFAULT NULL,
    source_of_contribution                          VARCHAR(255) DEFAULT NULL,
    legal_status                                    ENUM ('Private', 'Public', 'AutomaticPublic') DEFAULT NULL,
    application_form_partner_contribution_id        INT UNSIGNED DEFAULT NULL,
    amount_from_af                                  DECIMAL(17, 2) NOT NULL,
    previously_reported                             DECIMAL(17, 2) NOT NULL,
    currently_reported                              DECIMAL(17, 2) NOT NULL,
    CONSTRAINT fk_report_partner_spf_contribution_to_report_project
        FOREIGN KEY (report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
