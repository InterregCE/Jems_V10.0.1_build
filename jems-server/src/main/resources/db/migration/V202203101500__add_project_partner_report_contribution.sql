CREATE TABLE report_project_partner_contribution
(
    id                       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_id                INT UNSIGNED NOT NULL,
    source_of_contribution   VARCHAR(255) DEFAULT NULL,
    legal_status             ENUM ('Private', 'Public', 'AutomaticPublic') DEFAULT NULL,
    id_from_application_form INT UNSIGNED DEFAULT NULL,
    history_identifier       BINARY(16) NOT NULL,
    created_in_this_report   BOOLEAN        NOT NULL,
    amount                   DECIMAL(15, 2) NOT NULL,
    previously_reported      DECIMAL(15, 2) NOT NULL,
    currently_reported       DECIMAL(15, 2) NOT NULL,
    CONSTRAINT fk_report_partner_contribution_to_report_partner
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);
