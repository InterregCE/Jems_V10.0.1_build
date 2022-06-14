ALTER TABLE report_project_partner_identification
    ADD COLUMN current_report DECIMAL(17, 2) UNSIGNED NOT NULL,
    ADD COLUMN previously_reported DECIMAL(17, 2) UNSIGNED NOT NULL,
    ADD COLUMN next_report_forecast DECIMAL(17, 2) UNSIGNED NOT NULL;

ALTER TABLE report_project_partner_identification_transl
    ADD COLUMN spending_deviations TEXT(2000) DEFAULT NULL;

CREATE TABLE report_project_partner_budget_per_period
(
    report_id                INT UNSIGNED NOT NULL,
    period_number            SMALLINT UNSIGNED NOT NULL,
    period_budget            DECIMAL(17, 2) UNSIGNED NOT NULL,
    period_budget_cumulative DECIMAL(17, 2) UNSIGNED NOT NULL,
    start_month              SMALLINT UNSIGNED NOT NULL,
    end_month                SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY (report_id, period_number),
    CONSTRAINT fk_report_budget_per_period_to_report
        FOREIGN KEY (report_id) REFERENCES report_project_partner (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
)
