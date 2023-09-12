ALTER TABLE report_project_co_financing
    MODIFY COLUMN total DECIMAL (17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN `current` DECIMAL (17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported DECIMAL (17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_paid DECIMAL (17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_certificate_cost_category
    MODIFY COLUMN staff_total          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN office_total         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN travel_total         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN external_total       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN equipment_total      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN infrastructure_total DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN other_total          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN lump_sum_total       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN unit_cost_total      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN sum_total            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    MODIFY COLUMN staff_current          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN office_current         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN travel_current         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN external_current       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN equipment_current      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN infrastructure_current DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN other_current          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN lump_sum_current       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN unit_cost_current      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN sum_current            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    MODIFY COLUMN staff_previously_reported          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN office_previously_reported         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN travel_previously_reported         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN external_previously_reported       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN equipment_previously_reported      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN infrastructure_previously_reported DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN other_previously_reported          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN lump_sum_previously_reported       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN unit_cost_previously_reported      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN sum_previously_reported            DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_certificate_lump_sum
    MODIFY COLUMN total               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN current             DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_paid     DECIMAL(17, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE report_project_certificate_investment
    MODIFY COLUMN total               DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN `current`           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    MODIFY COLUMN previously_reported DECIMAL(17, 2) NOT NULL DEFAULT 0.00;
