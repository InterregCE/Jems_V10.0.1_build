CREATE TABLE project_partner_co_financing
(
    id                        INT UNSIGNED AUTO_INCREMENT PRIMARY KEY KEY,
    partner_id                INT UNSIGNED     NOT NULL,
    percentage                TINYINT UNSIGNED NOT NULL,
    programme_fund_id         INT UNSIGNED DEFAULT NULL,
    CONSTRAINT fk_project_partner_co_financing_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_co_financing_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
);

ALTER TABLE project_partner_budget_equipment
    ADD COLUMN row_sum DECIMAL(17, 2) UNSIGNED NOT NULL AFTER price_per_unit;
ALTER TABLE project_partner_budget_external
    ADD COLUMN row_sum DECIMAL(17, 2) UNSIGNED NOT NULL AFTER price_per_unit;
ALTER TABLE project_partner_budget_infrastructure
    ADD COLUMN row_sum DECIMAL(17, 2) UNSIGNED NOT NULL AFTER price_per_unit;
ALTER TABLE project_partner_budget_staff_cost
    ADD COLUMN row_sum DECIMAL(17, 2) UNSIGNED NOT NULL AFTER price_per_unit;
ALTER TABLE project_partner_budget_travel
    ADD COLUMN row_sum DECIMAL(17, 2) UNSIGNED NOT NULL AFTER price_per_unit;
