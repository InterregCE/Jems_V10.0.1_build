ALTER TABLE report_project_partner_contribution
    MODIFY COLUMN amount DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN previously_reported DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN currently_reported DECIMAL(17, 2) NOT NULL;

ALTER TABLE report_project_partner_co_financing
    MODIFY COLUMN total DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN current DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN total_eligible_after_control DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN previously_reported DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN previously_validated DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN previously_paid DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN current_parked DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN current_re_included DECIMAL(17, 2) NOT NULL,
    MODIFY COLUMN previously_reported_parked DECIMAL(17, 2) NOT NULL;
