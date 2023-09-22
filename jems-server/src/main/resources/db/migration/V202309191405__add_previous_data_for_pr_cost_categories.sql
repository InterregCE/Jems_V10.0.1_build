ALTER TABLE report_project_certificate_cost_category
    ADD COLUMN staff_previously_verified          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN office_previously_verified         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN travel_previously_verified         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN external_previously_verified       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN equipment_previously_verified      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN infrastructure_previously_verified DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN other_previously_verified          DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN lump_sum_previously_verified       DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN unit_cost_previously_verified      DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN sum_previously_verified            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,

    ADD COLUMN staff_current_verified            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN office_current_verified           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN travel_current_verified           DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN external_current_verified         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN equipment_current_verified        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN infrastructure_current_verified   DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN other_current_verified            DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN lump_sum_current_verified         DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN unit_cost_current_verified        DECIMAL(17, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN sum_current_verified              DECIMAL(17, 2) NOT NULL DEFAULT 0.00;


ALTER TABLE report_project_certificate_investment
    ADD COLUMN current_verified DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported,
    ADD COLUMN previously_verified DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_verified;


ALTER TABLE report_project_certificate_lump_sum
    ADD COLUMN current_verified    DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported,
    ADD COLUMN previously_verified DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_verified;


ALTER TABLE report_project_certificate_unit_cost
    ADD COLUMN current_verified    DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported,
    ADD COLUMN previously_verified DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_verified;