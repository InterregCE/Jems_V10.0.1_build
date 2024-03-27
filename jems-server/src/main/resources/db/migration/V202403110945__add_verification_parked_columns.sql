ALTER TABLE report_project_partner_expenditure_co_financing
    ADD COLUMN partner_contribution_current_parked_verification                     DECIMAL(17, 2)  NOT NULL DEFAULT 0 after sum_previously_validated,
    ADD COLUMN public_contribution_current_parked_verification                      DECIMAL(17, 2)  NOT NULL DEFAULT 0 after partner_contribution_current_parked_verification,
    ADD COLUMN automatic_public_contribution_current_parked_verification            DECIMAL(17, 2)  NOT NULL DEFAULT 0 after public_contribution_current_parked_verification,
    ADD COLUMN private_contribution_current_parked_verification                     DECIMAL(17, 2)  NOT NULL DEFAULT 0 after automatic_public_contribution_current_parked_verification,
    ADD COLUMN sum_current_parked_verification                                      DECIMAL(17, 2)  NOT NULL DEFAULT 0 after private_contribution_current_parked_verification;



ALTER TABLE report_project_partner_co_financing
    ADD COLUMN current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER previously_reported_parked;


ALTER TABLE report_project_partner_expenditure_cost_category
    ADD COLUMN staff_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after sum_current_parked,
    ADD COLUMN office_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after staff_current_parked_verification,
    ADD COLUMN travel_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after office_current_parked_verification,
    ADD COLUMN external_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after travel_current_parked_verification,
    ADD COLUMN equipment_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after external_current_parked_verification,
    ADD COLUMN infrastructure_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after equipment_current_parked_verification,
    ADD COLUMN other_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after infrastructure_current_parked_verification,
    ADD COLUMN lump_sum_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after other_current_parked_verification,
    ADD COLUMN unit_cost_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after lump_sum_current_parked_verification,
    ADD COLUMN spf_cost_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after unit_cost_current_parked_verification,
    ADD COLUMN sum_current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0 after spf_cost_current_parked_verification;

ALTER TABLE report_project_partner_lump_sum
    ADD COLUMN current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_parked;

ALTER TABLE report_project_partner_unit_cost
    ADD COLUMN current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_parked;


ALTER TABLE report_project_partner_investment
    ADD COLUMN current_parked_verification DECIMAL(17, 2) NOT NULL DEFAULT 0.00 AFTER current_parked;
