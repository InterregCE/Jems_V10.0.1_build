CREATE TABLE report_project_spending_profile
(
    project_report_id   INT UNSIGNED NOT NULL,
    partner_id          INT UNSIGNED NOT NULL,

    previously_reported DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,
    currently_reported  DECIMAL(17, 2) UNSIGNED NOT NULL DEFAULT 0.00,

    PRIMARY KEY (project_report_id, partner_id),

    CONSTRAINT fk_report_project_spending_profile_to_project_report_id
        FOREIGN KEY (project_report_id) REFERENCES report_project(id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,
    CONSTRAINT fk_report_project_spending_profile_to_partner_id
        FOREIGN KEY (partner_id) REFERENCES project_partner (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
)
