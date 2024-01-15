ALTER TABLE report_project_partner_expenditure_co_financing
    ADD COLUMN partner_contribution_previously_reported_spf          DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER sum_previously_reported_parked,
    ADD COLUMN public_contribution_previously_reported_spf           DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER partner_contribution_previously_reported_spf,
    ADD COLUMN automatic_public_contribution_previously_reported_spf DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER public_contribution_previously_reported_spf,
    ADD COLUMN private_contribution_previously_reported_spf          DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER automatic_public_contribution_previously_reported_spf,
    ADD COLUMN sum_previously_reported_spf                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER private_contribution_previously_reported_spf;

ALTER TABLE report_project_partner_expenditure_cost_category
    ADD COLUMN spf_cost_current                      DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_current,
    ADD COLUMN spf_cost_total_eligible_after_control DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_total_eligible_after_control,
    ADD COLUMN spf_cost_previously_reported          DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_previously_reported,
    ADD COLUMN spf_cost_previously_validated         DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_previously_validated,
    ADD COLUMN spf_cost_current_parked               DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_current_parked,
    ADD COLUMN spf_cost_current_re_included          DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_current_re_included,
    ADD COLUMN spf_cost_previously_reported_parked   DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_previously_reported_parked;

ALTER TABLE report_project_partner_co_financing
    ADD COLUMN previously_reported_spf DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported_parked;

ALTER TABLE report_project_certificate_cost_category
    ADD COLUMN spf_cost_current             DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_current,
    ADD COLUMN spf_cost_previously_reported DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_previously_reported,
    ADD COLUMN spf_cost_previously_verified DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_previously_verified,
    ADD COLUMN spf_cost_current_verified    DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER unit_cost_current_verified;

ALTER TABLE report_project
    ADD COLUMN spf_partner_id INT UNSIGNED DEFAULT NULL AFTER lead_partner_name_in_english,
    ADD CONSTRAINT fk_default_spf_partner_from_project_report
        FOREIGN KEY (spf_partner_id) REFERENCES project_partner (id);

CREATE TABLE report_project_verification_contribution_spf_source_overview
(
    id                              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    project_report_id               INT UNSIGNED,
    fund_id                         INT UNSIGNED DEFAULT NULL,
    fund_value                      DECIMAL(17, 2) DEFAULT NULL,
    partner_contribution            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    public_contribution             DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    automatic_public_contribution   DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    private_contribution            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    total                           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    UNIQUE (project_report_id, fund_id),

    CONSTRAINT fk_rpv_contribution_spf_source_overview_to_report_project
        FOREIGN KEY (project_report_id) REFERENCES report_project (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT,

    CONSTRAINT fk_rpv_contribution_spf_source_overview_to_programme_fund
        FOREIGN KEY (fund_id) REFERENCES programme_fund (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT
);
