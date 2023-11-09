ALTER TABLE report_project_partner_investment
    MODIFY COLUMN total                      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN `current`                  DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported_parked DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN current_re_included        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN current_parked             DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_partner_unit_cost
    MODIFY COLUMN total                      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN `current`                  DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported_parked DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN current_re_included        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN current_parked             DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_partner_lump_sum
    MODIFY COLUMN total                      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN `current`                  DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_paid            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported_parked DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN current_re_included        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN current_parked             DECIMAL(17, 2) NOT NULL DEFAULT 0.00;
