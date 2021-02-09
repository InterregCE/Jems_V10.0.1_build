ALTER TABLE project_call
    ADD COLUMN is_additional_fund_allowed BOOLEAN NOT NULL DEFAULT FALSE;

DROP TABLE project_partner_co_financing;

CREATE TABLE project_partner_co_financing
(
    partner_id        INT UNSIGNED                                               NOT NULL,
    type              ENUM ('PartnerContribution', 'MainFund', 'AdditionalFund') NOT NULL,
    percentage        TINYINT UNSIGNED                                           NOT NULL,
    programme_fund_id INT UNSIGNED DEFAULT NULL,
    PRIMARY KEY (partner_id, type),
    CONSTRAINT fk_project_partner_co_financing_to_project_partner FOREIGN KEY (partner_id) REFERENCES project_partner (id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_project_partner_co_financing_to_programme_fund FOREIGN KEY (programme_fund_id) REFERENCES programme_fund (id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
);