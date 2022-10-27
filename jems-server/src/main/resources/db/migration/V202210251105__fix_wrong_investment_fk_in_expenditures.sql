ALTER TABLE report_project_partner_expenditure_investment
    RENAME TO report_project_partner_investment;

CREATE TABLE report_project_partner_investment_transl
(
    source_entity_id INT UNSIGNED NOT NULL,
    language         VARCHAR(3)   NOT NULL,
    title            VARCHAR(50)  NOT NULL,
    PRIMARY KEY (source_entity_id, language),
    CONSTRAINT fk_report_investment_transl_to_report_project_partner_investment
        FOREIGN KEY (source_entity_id) REFERENCES report_project_partner_investment (id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
);

ALTER TABLE report_project_partner_expenditure
    DROP COLUMN investment_id,
    ADD COLUMN report_investment_id INT UNSIGNED DEFAULT NULL AFTER report_unit_cost_id,
    ADD CONSTRAINT fk_report_expenditure_investment_to_report_investment
        FOREIGN KEY (report_investment_id) REFERENCES report_project_partner_investment (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT;
